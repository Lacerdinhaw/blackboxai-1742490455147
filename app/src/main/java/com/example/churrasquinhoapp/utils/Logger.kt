package com.example.churrasquinhoapp.utils

import android.util.Log
import com.example.churrasquinhoapp.BuildConfig

object Logger {
    private const val TAG = "ChurrasquinhoApp"
    private val isDebug = BuildConfig.DEBUG

    fun d(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.d(tag, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun i(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.i(tag, message)
        }
    }

    fun w(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }

    fun v(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.v(tag, message)
        }
    }

    fun methodEntry(methodName: String) {
        if (isDebug) {
            Log.d(TAG, "→ $methodName")
        }
    }

    fun methodExit(methodName: String) {
        if (isDebug) {
            Log.d(TAG, "← $methodName")
        }
    }

    fun dbOperation(operation: String, details: String) {
        if (isDebug) {
            Log.d(TAG, "DB Operation: $operation - $details")
        }
    }

    fun uiEvent(event: String, details: String) {
        if (isDebug) {
            Log.d(TAG, "UI Event: $event - $details")
        }
    }

    fun lifecycleEvent(event: String, component: String) {
        if (isDebug) {
            Log.d(TAG, "Lifecycle: $component - $event")
        }
    }

    fun performance(operation: String, durationMs: Long) {
        if (isDebug) {
            Log.d(TAG, "Performance: $operation took ${durationMs}ms")
        }
    }

    fun stateChange(component: String, oldState: String, newState: String) {
        if (isDebug) {
            Log.d(TAG, "State Change in $component: $oldState → $newState")
        }
    }

    fun exception(throwable: Throwable) {
        Log.e(TAG, "Exception: ${throwable.message}")
        Log.e(TAG, "Stack trace: ${Log.getStackTraceString(throwable)}")
    }

    fun memoryUsage() {
        if (isDebug) {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val maxMemory = runtime.maxMemory() / 1024 / 1024

            Log.d(TAG, """
                Memory Usage:
                - Used: $usedMemory MB
                - Free: $freeMemory MB
                - Total: $totalMemory MB
                - Max: $maxMemory MB
            """.trimIndent())
        }
    }
}