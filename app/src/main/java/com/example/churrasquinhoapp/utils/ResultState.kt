package com.example.churrasquinhoapp.utils

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with an error message.
 */
sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val exception: Exception? = null) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    object Empty : ResultState<Nothing>()

    /**
     * Returns true if this is a Success
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this is Loading
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns true if this is Empty
     */
    val isEmpty: Boolean get() = this is Empty

    /**
     * Returns the encapsulated data if this instance represents [Success] or null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the encapsulated data if this instance represents [Success] or throws the exception if it is [Error]
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: Exception(message)
        is Loading -> throw IllegalStateException("Result is Loading")
        is Empty -> throw IllegalStateException("Result is Empty")
    }

    /**
     * Maps the success value of this ResultState to a new value using the given transform
     */
    inline fun <R> map(transform: (T) -> R): ResultState<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, exception)
        is Loading -> Loading
        is Empty -> Empty
    }

    /**
     * Returns the encapsulated data if this instance represents [Success] or the default value otherwise
     */
    fun getOrDefault(defaultValue: T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    /**
     * Performs the given action if this instance represents [Success]
     */
    inline fun onSuccess(action: (T) -> Unit): ResultState<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Performs the given action if this instance represents [Error]
     */
    inline fun onError(action: (String, Exception?) -> Unit): ResultState<T> {
        if (this is Error) action(message, exception)
        return this
    }

    /**
     * Performs the given action if this instance represents [Loading]
     */
    inline fun onLoading(action: () -> Unit): ResultState<T> {
        if (this is Loading) action()
        return this
    }

    /**
     * Performs the given action if this instance represents [Empty]
     */
    inline fun onEmpty(action: () -> Unit): ResultState<T> {
        if (this is Empty) action()
        return this
    }

    companion object {
        /**
         * Creates a ResultState.Success with the given data
         */
        fun <T> success(data: T): ResultState<T> = Success(data)

        /**
         * Creates a ResultState.Error with the given message and optional exception
         */
        fun error(message: String, exception: Exception? = null): ResultState<Nothing> = 
            Error(message, exception)

        /**
         * Creates a ResultState.Loading
         */
        fun loading(): ResultState<Nothing> = Loading

        /**
         * Creates a ResultState.Empty
         */
        fun empty(): ResultState<Nothing> = Empty

        /**
         * Wraps a suspending operation in a ResultState
         */
        suspend fun <T> wrap(block: suspend () -> T): ResultState<T> = try {
            Success(block())
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error occurred", e)
        }
    }
}