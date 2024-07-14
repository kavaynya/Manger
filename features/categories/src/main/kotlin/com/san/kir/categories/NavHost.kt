package com.san.kir.categories

import NavEntry
import com.san.kir.categories.ui.categories.CategoriesScreen
import com.san.kir.categories.ui.category.CategoryScreen
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import kotlinx.serialization.Serializable


public fun categoryNavigationCreators() {
    AddNavigationCreators
}

@NavEntry
@Serializable
public object Categories : NavConfig() {
    internal val creator = navCreator<Categories> {
        CategoriesScreen(
            navigateUp = backPressed(),
            navigateToItem = add(::Category)
        )
    }

    internal val animation = navAnimation<Categories> { horizontalSlide() }
}

@NavEntry
@Serializable
public class Category(internal val name: String, internal val params: SharedParams) : NavConfig() {
    internal companion object {
        val creator = navCreator<Category> { config ->
            CategoryScreen(
                navigateUp = backPressed(),
                categoryName = config.name
            )
        }

        val animation = navAnimation<Category> { shapeAnimator(it.params) }
    }
}
