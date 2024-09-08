package com.san.kir.data.models.main

import com.san.kir.data.models.utils.ChapterFilter
import com.san.kir.data.models.utils.Orientation

public data class Settings(
    val isFirstLaunch: Boolean = true,
    val chapters: Chapters = Chapters(),
    val download: Download = Download(),
    val main: Main = Main(),
    val viewer: Viewer = Viewer(),
) {

    public data class Chapters(
        val isIndividual: Boolean = true,
        val isTitle: Boolean = true,
        val filterStatus: ChapterFilter = ChapterFilter.ALL_READ_ASC,
    )

    public data class Download(
        val concurrent: Boolean = true,
        val retry: Boolean = false,
        val wifi: Boolean = false,
    )

    public data class Main(
        val isDarkTheme: Boolean = true,
        val isShowCategory: Boolean = true,
        val editMenu: Boolean = false,
    )

    public data class Viewer(
        val orientation: Orientation = Orientation.AUTO_LAND,
        val cutOut: Boolean = true,
        val control: Control = Control(),
        val withoutSaveFiles: Boolean = false,
        val useScrollbars: Boolean = true,
    ) {
        val controls: List<Boolean>
            get() = listOf(control.taps, control.swipes, control.keys)

        public fun controls(items: List<Boolean>): Control = Control(items[0], items[1], items[2])

        public data class Control(
            val taps: Boolean = false,
            val swipes: Boolean = true,
            val keys: Boolean = false,
        )
    }

}
