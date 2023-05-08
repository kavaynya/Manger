package com.san.kir.categories

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.categories.ui.categories.CategoriesScreen
import com.san.kir.categories.ui.category.CategoryScreen
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import kotlinx.parcelize.Parcelize

@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            CategoriesScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Category)
            )
        }
    }
}

@Parcelize
internal class Category(val name: String, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Category> { config ->
            CategoryScreen(
                navigateUp = backPressed(),
                categoryName = config.name
            )
        }
    }
}

@Composable
fun CategoriesNavHost() {
    NavHost(
        startConfig = Main(),
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Category -> Category.creator(config)
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
        is Category -> shapeAnimator(initial.params)
        else -> EmptyStackAnimator
    }
}
