package com.example.churrasquinhoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {
    private var connectivityManager: ConnectivityManager? = null
    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    /**
     * Initialize NetworkUtils with application context
     */
    fun init(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        setupNetworkCallback()
    }

    /**
     * Set up network callback to monitor connectivity changes
     */
    private fun setupNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.postValue(true)
            }

            override fun onLost(network: Network) {
                _isNetworkAvailable.postValue(false)
            }

            override fun onUnavailable() {
                _isNetworkAvailable.postValue(false)
            }
        }

        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * Check if network is currently available
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    /**
     * Check if device has internet connectivity
     */
    suspend fun hasInternetConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            socket.connect(socketAddress, 1500)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Get current network type
     */
    fun getNetworkType(context: Context): NetworkType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.CELLULAR
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
                else -> NetworkType.NONE
            }
        } else {
            @Suppress("DEPRECATION")
            when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
                ConnectivityManager.TYPE_ETHERNET -> NetworkType.ETHERNET
                else -> NetworkType.NONE
            }
        }
    }

    /**
     * Get network connection quality
     */
    fun getConnectionQuality(context: Context): ConnectionQuality {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            return when {
                capabilities == null -> ConnectionQuality.UNKNOWN
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) -> ConnectionQuality.EXCELLENT
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionQuality.GOOD
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    when {
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED) -> ConnectionQuality.GOOD
                        else -> ConnectionQuality.POOR
                    }
                }
                else -> ConnectionQuality.UNKNOWN
            }
        }
        return ConnectionQuality.UNKNOWN
    }

    /**
     * Network type enum
     */
    enum class NetworkType {
        WIFI,
        CELLULAR,
        ETHERNET,
        NONE
    }

    /**
     * Connection quality enum
     */
    enum class ConnectionQuality {
        EXCELLENT,
        GOOD,
        POOR,
        UNKNOWN
    }

    /**
     * Network error handling
     */
    sealed class NetworkError : Exception() {
        object NoConnection : NetworkError()
        object Timeout : NetworkError()
        data class ServerError(override val message: String) : NetworkError()
        data class Unknown(override val message: String) : NetworkError()
    }

    /**
     * Extension function to handle network errors
     */
    suspend fun <T> withNetworkHandler(block: suspend () -> T): Result<T> {
        return try {
            if (!hasInternetConnection()) {
                Result.failure(NetworkError.NoConnection)
            } else {
                Result.success(block())
            }
        } catch (e: Exception) {
            val networkError = when (e) {
                is java.net.SocketTimeoutException -> NetworkError.Timeout
                is java.net.UnknownHostException -> NetworkError.NoConnection
                is IOException -> NetworkError.ServerError(e.message ?: "Server Error")
                else -> NetworkError.Unknown(e.message ?: "Unknown Error")
            }
            Result.failure(networkError)
        }
    }
}