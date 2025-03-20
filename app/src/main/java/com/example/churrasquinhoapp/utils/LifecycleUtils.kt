package com.example.churrasquinhoapp.utils

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object LifecycleUtils {
    /**
     * Lifecycle-aware coroutine launcher
     */
    fun LifecycleOwner.launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }

    fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                block()
            }
        }
    }

    /**
     * Flow collectors with lifecycle awareness
     */
    fun <T> Flow<T>.collectWhenStarted(
        lifecycleOwner: LifecycleOwner,
        collector: suspend (T) -> Unit
    ): Job {
        return lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { collector(it) }
            }
        }
    }

    fun <T> Flow<T>.collectWhenResumed(
        lifecycleOwner: LifecycleOwner,
        collector: suspend (T) -> Unit
    ): Job {
        return lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                collect { collector(it) }
            }
        }
    }

    /**
     * LiveData transformations with lifecycle awareness
     */
    fun <T> LiveData<T>.observeOnce(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<T>
    ) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    fun <T> LiveData<T>.observeWithPrevious(
        lifecycleOwner: LifecycleOwner,
        observer: (previous: T?, current: T) -> Unit
    ) {
        var previousValue: T? = null
        observe(lifecycleOwner) { currentValue ->
            observer(previousValue, currentValue)
            previousValue = currentValue
        }
    }

    /**
     * Lifecycle event observer
     */
    class LifecycleEventObserver(
        private val onCreate: (() -> Unit)? = null,
        private val onStart: (() -> Unit)? = null,
        private val onResume: (() -> Unit)? = null,
        private val onPause: (() -> Unit)? = null,
        private val onStop: (() -> Unit)? = null,
        private val onDestroy: (() -> Unit)? = null
    ) : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            onCreate?.invoke()
        }

        override fun onStart(owner: LifecycleOwner) {
            onStart?.invoke()
        }

        override fun onResume(owner: LifecycleOwner) {
            onResume?.invoke()
        }

        override fun onPause(owner: LifecycleOwner) {
            onPause?.invoke()
        }

        override fun onStop(owner: LifecycleOwner) {
            onStop?.invoke()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onDestroy?.invoke()
        }
    }

    /**
     * Extension function to add lifecycle event observer
     */
    fun LifecycleOwner.observeLifecycleEvents(
        onCreate: (() -> Unit)? = null,
        onStart: (() -> Unit)? = null,
        onResume: (() -> Unit)? = null,
        onPause: (() -> Unit)? = null,
        onStop: (() -> Unit)? = null,
        onDestroy: (() -> Unit)? = null
    ) {
        lifecycle.addObserver(
            LifecycleEventObserver(
                onCreate,
                onStart,
                onResume,
                onPause,
                onStop,
                onDestroy
            )
        )
    }

    /**
     * Lifecycle state checks
     */
    fun LifecycleOwner.isAtLeastStarted(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    fun LifecycleOwner.isAtLeastResumed(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    /**
     * Lifecycle-aware resource management
     */
    fun <T : AutoCloseable> T.bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                try {
                    close()
                } catch (e: Exception) {
                    Logger.e("Error closing resource", e)
                }
            }
        })
    }

    /**
     * Lifecycle-aware error handling
     */
    fun LifecycleOwner.handleError(
        error: Throwable,
        showError: (String) -> Unit
    ) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            showError(error.message ?: "Unknown error occurred")
        }
    }

    /**
     * Lifecycle state enum
     */
    enum class LifecycleState {
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED,
        DESTROYED
    }

    /**
     * Extension function to get current lifecycle state
     */
    fun LifecycleOwner.getCurrentState(): LifecycleState {
        return when (lifecycle.currentState) {
            Lifecycle.State.INITIALIZED -> LifecycleState.INITIALIZED
            Lifecycle.State.CREATED -> LifecycleState.CREATED
            Lifecycle.State.STARTED -> LifecycleState.STARTED
            Lifecycle.State.RESUMED -> LifecycleState.RESUMED
            Lifecycle.State.DESTROYED -> LifecycleState.DESTROYED
        }
    }
}