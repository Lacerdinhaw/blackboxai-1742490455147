package com.example.churrasquinhoapp.utils

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Utility object for handling coroutine-related operations
 */
object CoroutineUtils {
    /**
     * Default error handler for coroutines
     */
    private val defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        println("Coroutine error: ${throwable.message}")
        throwable.printStackTrace()
    }

    /**
     * Creates a coroutine scope with error handling
     */
    fun createSafeScope(
        context: CoroutineContext = EmptyCoroutineContext,
        errorHandler: CoroutineExceptionHandler = defaultErrorHandler
    ): CoroutineScope {
        return CoroutineScope(context + errorHandler)
    }

    /**
     * Executes a suspending operation with error handling
     */
    suspend fun <T> executeSafely(
        operation: suspend () -> T,
        onError: (Exception) -> Unit = { it.printStackTrace() }
    ): T? {
        return try {
            operation()
        } catch (e: Exception) {
            onError(e)
            null
        }
    }

    /**
     * Extension function to launch a coroutine with error handling in a lifecycle scope
     */
    fun LifecycleCoroutineScope.launchSafely(
        context: CoroutineContext = EmptyCoroutineContext,
        onError: (Exception) -> Unit = { it.printStackTrace() },
        block: suspend CoroutineScope.() -> Unit
    ) {
        launch(context + defaultErrorHandler) {
            try {
                block()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /**
     * Extension function to execute an operation with retry logic
     */
    suspend fun <T> withRetry(
        times: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block() // last attempt
    }

    /**
     * Extension function to execute an operation with timeout
     */
    suspend fun <T> withTimeout(
        timeMillis: Long,
        onTimeout: () -> T,
        block: suspend () -> T
    ): T {
        return try {
            kotlinx.coroutines.withTimeout(timeMillis) {
                block()
            }
        } catch (e: TimeoutCancellationException) {
            onTimeout()
        }
    }

    /**
     * Extension function to execute parallel operations
     */
    suspend fun <T> executeParallel(
        dispatchers: CoroutineDispatcher = Dispatchers.Default,
        vararg operations: suspend () -> T
    ): List<T> {
        return coroutineScope {
            operations.map { operation ->
                async(dispatchers) {
                    operation()
                }
            }.awaitAll()
        }
    }

    /**
     * Extension function to execute operations sequentially with progress updates
     */
    suspend fun <T> executeSequentially(
        operations: List<suspend () -> T>,
        onProgress: suspend (Int, Int) -> Unit
    ): List<T> {
        val results = mutableListOf<T>()
        operations.forEachIndexed { index, operation ->
            results.add(operation())
            onProgress(index + 1, operations.size)
        }
        return results
    }

    /**
     * Extension function to execute an operation with rate limiting
     */
    suspend fun <T> withRateLimit(
        limitPerSecond: Int,
        block: suspend () -> T
    ): T {
        val minDelayMillis = 1000L / limitPerSecond
        val lastExecutionTime = System.currentTimeMillis()
        val timeSinceLastExecution = System.currentTimeMillis() - lastExecutionTime
        
        if (timeSinceLastExecution < minDelayMillis) {
            delay(minDelayMillis - timeSinceLastExecution)
        }
        
        return block()
    }

    /**
     * Extension function to execute an operation with circuit breaker pattern
     */
    class CircuitBreaker(
        private val maxFailures: Int = 3,
        private val resetTimeoutMillis: Long = 5000
    ) {
        private var failures = 0
        private var lastFailureTime = 0L

        suspend fun <T> execute(block: suspend () -> T): T {
            if (failures >= maxFailures) {
                val timeSinceLastFailure = System.currentTimeMillis() - lastFailureTime
                if (timeSinceLastFailure < resetTimeoutMillis) {
                    throw CircuitBreakerOpenException()
                }
                failures = 0
            }

            try {
                return block()
            } catch (e: Exception) {
                failures++
                lastFailureTime = System.currentTimeMillis()
                throw e
            }
        }
    }

    class CircuitBreakerOpenException : Exception("Circuit breaker is open")
}