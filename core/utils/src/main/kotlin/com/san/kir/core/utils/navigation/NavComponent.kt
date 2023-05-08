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
    fun <P1> add(config: (P1) -> NavConfig): (P1) -> Unit

    @Composable
    fun <P1, P2> add(config: (P1, P2) -> NavConfig): (P1, P2) -> Unit

    fun <P1> simpleAdd(config: (P1) -> NavConfig): (P1) -> Unit
    fun <P1, P2> simpleAdd(config: (P1, P2) -> NavConfig): (P1, P2) -> Unit

    @Composable
    fun back(): () -> Unit
}

interface NavComponent<C : NavConfig> {
    val config: C

    @Composable
    fun NavComponentScope.Render()
}
