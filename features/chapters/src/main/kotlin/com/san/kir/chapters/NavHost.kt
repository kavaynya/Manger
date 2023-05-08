package com.san.kir.chapters

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.san.kir.chapters.ui.chapters.ChaptersScreen
import com.san.kir.chapters.ui.latest.LatestScreen
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.features.viewer.MangaViewer
import kotlinx.parcelize.Parcelize

@Parcelize
class Chapters(val mangaId: Long, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Chapters> { config ->
            val context = LocalContext.current
            ChaptersScreen(
                navigateUp = backPressed(),
                navigateToViewer = rememberLambda { id: Long -> MangaViewer.start(context, id) },
                mangaId = config.mangaId
            )
        }
    }
}

object LatestCreator {
    @Composable
    operator fun invoke() {
        val context = LocalContext.current
        LatestScreen(
            navigateUp = backPressed(),
            navigateToViewer = rememberLambda { id: Long -> MangaViewer.start(context, id) },
        )
    }
}
