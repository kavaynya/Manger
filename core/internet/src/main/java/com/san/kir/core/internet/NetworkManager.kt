package com.san.kir.core.internet

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.san.kir.core.utils.connectivityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class NetworkState {
    NOT_WIFI, NOT_CELLURAR, OK
}

class CellularNetwork(context: Application) : NetworkManager(
    context, NetworkCapabilities.TRANSPORT_CELLULAR
)

class WifiNetwork(context: Application) : NetworkManager(
    context, NetworkCapabilities.TRANSPORT_WIFI,
)

abstract class NetworkManager(
    private val context: Context,
    networkTransport: Int,
) : ConnectivityManager.NetworkCallback() {

    private val request =
        NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(networkTransport)
            .build()

    private val _state = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        context.connectivityManager.registerNetworkCallback(request, this)
    }

    fun stop() {
        context.connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        _state.update { true }
    }

    override fun onLost(network: Network) {
        _state.update { false }
    }
}
