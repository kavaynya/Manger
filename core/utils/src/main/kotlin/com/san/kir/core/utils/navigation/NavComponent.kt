package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.essenty.parcelable.Parcelable

interface NavConfig : Parcelable

interface NavComponentScope {

    @Composable
    fun replace(config: NavConfig): () -> Unit

    @Composable
    fun add(config: NavConfig): () -> Unit

    fun simpleAdd(config: NavConfig): () -> Unit

    @Composable
    fun <C> add(config: (C) -> NavConfig): (C) -> Unit

    fun <C> simpleAdd(config: (C) -> NavConfig): (C) -> Unit

    @Composable
    fun back(): () -> Unit
}

interface NavComponent<C : NavConfig> {
    val config: C

    @Composable
    fun NavComponentScope.Render()
}
