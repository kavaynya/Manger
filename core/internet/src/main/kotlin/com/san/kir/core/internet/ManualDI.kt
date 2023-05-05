package com.san.kir.core.internet

import com.san.kir.core.utils.ManualDI

private var connectManagerInstance: ConnectManager? = null

val ManualDI.connectManager: ConnectManager
    get() {
        return connectManagerInstance ?: run {
            val instance = ConnectManager(context)
            connectManagerInstance = instance
            return instance
        }
    }

val ManualDI.cellularNetwork: CellularNetwork
    get() = CellularNetwork(context)

val ManualDI.wifiNetwork: WifiNetwork
    get() = WifiNetwork(context)
