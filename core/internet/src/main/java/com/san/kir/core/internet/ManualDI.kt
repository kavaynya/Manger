package com.san.kir.core.internet

import com.san.kir.core.utils.ManualDI

fun ManualDI.connectManager() = ConnectManager(application)
fun ManualDI.cellularNetwork(): NetworkManager = CellularNetwork(application)
fun ManualDI.wifiNetwork(): NetworkManager = WifiNetwork(application)
