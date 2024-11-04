package com.san.kir.chapters

import NavEntry
import com.san.kir.catalog.GlobalSearch
import com.san.kir.chapters.ui.chapters.ChaptersScreen
import com.san.kir.chapters.ui.download.DownloadsScreen
import com.san.kir.chapters.ui.latest.LatestScreen
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.features.viewer.MangaViewer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

public val chaptersSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

@NavEntry
@Serializable
public class Chapters(internal val mangaId: Long, internal val params: SharedParams) : NavConfig() {
    internal companion object {
        val creator = navCreator<Chapters> { config ->
            ChaptersScreen(
                navigateUp = backPressed(),
                navigateToViewer = rememberLambda(MangaViewer::start),
                navigateToGlobalSearch = add(::GlobalSearch),
                mangaId = config.mangaId
            )
        }

        val animation = navAnimation<Chapters> { config -> itemShapeAnimator(config.params) }
    }
}

@NavEntry
@Serializable
public data object Downloads : NavConfig() {
    internal val creator = navCreator<Downloads> { DownloadsScreen(backPressed()) }
    internal val animation = navAnimation<Downloads> { horizontalSlide() }
}

@NavEntry
@Serializable
public data object Latest : NavConfig() {
    internal val creator = navCreator<Latest> {
        LatestScreen(
            navigateUp = backPressed(),
            navigateToViewer = rememberLambda(MangaViewer::start)
        )
    }
    internal val animation = navAnimation<Latest> { horizontalSlide() }
}
