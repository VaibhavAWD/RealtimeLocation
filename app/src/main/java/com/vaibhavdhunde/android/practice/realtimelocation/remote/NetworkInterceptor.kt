package com.vaibhavdhunde.android.practice.realtimelocation.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasConnection()) {
            throw NetworkException("No internet connection. Make sure you have active connection.")
        }
        return chain.proceed(chain.request())
    }

    private fun hasConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var hasConnection = false
            connectivityManager.let {
                it.getNetworkCapabilities(it.activeNetwork)?.apply {
                    hasConnection = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        else -> false
                    }
                }
            }
            return hasConnection
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo.also {
                return it != null && it.isConnected
            }
        }
    }

}