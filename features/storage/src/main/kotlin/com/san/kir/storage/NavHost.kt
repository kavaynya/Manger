package com.san.kir.storage

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.storage.ui.storage.StorageScreen
import com.san.kir.storage.ui.storages.StoragesScreen
import kotlinx.parcelize.Parcelize

@Parcelize
private class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            StoragesScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Storage)
            )
        }
    }
}

@Parcelize
class Storage(
    val mangaId: Long,
    val params: SharedParams,
    val hasUpdate: Boolean = false,
) : NavConfig {
    companion object {
        val creator = navCreator<Storage> { config ->
            StorageScreen(
                navigateUp = backPressed(),
                mangaId = config.mangaId,
                hasUpdate = config.hasUpdate
            )
        }
    }
}

@Composable
fun StorageNavHost(
    mangaId: Long? = null,
    params: SharedParams? = null,
    hasUpdate: Boolean = false,
) {
    NavHost(
        startConfig = mangaId?.let { params?.let { p -> Storage(it, p, hasUpdate) } } ?: Main(),
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Storage -> Storage.creator(config)
            else -> null
        }
    }
}

private val animation = stackAnimation<NavConfig, NavContainer> { initial, target, direction ->
    if (direction.isFront) frontAnimation(initial.configuration)
    else frontAnimation(target.configuration)
}

private fun frontAnimation(initial: NavConfig): StackAnimator {
    return when (initial) {
        is Storage -> shapeAnimator(initial.params)
        else -> EmptyStackAnimator
    }
}
