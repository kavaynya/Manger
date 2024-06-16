package com.san.kir.core.utils.navigation

import com.arkivanov.decompose.ComponentContext

public data class NavContainer(
    val context: ComponentContext,
    val component: NavComponent<*>?
)
