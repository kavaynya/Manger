package com.san.kir.core.internet

import android.net.NetworkCapabilities
import com.san.kir.core.utils.ManualDI

public fun ManualDI.connectManager(): ConnectManager = ConnectManager(application)
public fun ManualDI.cellularNetwork(): INetworkManager =
    NetworkManager(application, NetworkCapabilities.TRANSPORT_CELLULAR)

public fun ManualDI.wifiNetwork(): INetworkManager =
    NetworkManager(application, NetworkCapabilities.TRANSPORT_WIFI)
