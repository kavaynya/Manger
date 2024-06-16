package com.san.kir.core.utils.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.backhandler.BackCallback

public class NavBackComponent(
    componentContext: ComponentContext,
    private val navigation: StackNavigation<NavConfig>
) : ComponentContext by componentContext, NavBackHandler {

    override fun backPress(): Unit = navigation.pop()
    override fun isRegistered(callback: BackCallback): Boolean = backHandler.isRegistered(callback)
    override fun register(callback: BackCallback): Unit = backHandler.register(callback)
    override fun unregister(callback: BackCallback): Unit = backHandler.unregister(callback)
}
