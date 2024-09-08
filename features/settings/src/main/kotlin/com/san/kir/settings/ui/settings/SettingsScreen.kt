package com.san.kir.settings.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.Settings
import com.san.kir.data.models.utils.Orientation
import com.san.kir.settings.R
import com.san.kir.settings.utils.ListPreferenceItem
import com.san.kir.settings.utils.MultiSelectListPreferenceItem
import com.san.kir.settings.utils.PreferenceTitle
import com.san.kir.settings.utils.TogglePreferenceItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    navigateUp: () -> Unit,
) {
    val holder: SettingsStateHolder = stateHolder { SettingsViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.settings),
        ),
        additionalPadding = Dimensions.zero
    ) {
        Column(modifier = Modifier.bottomInsetsPadding()) {
            Main(state.main, sendAction)
            HorizontalDivider()

            Chapters(state.chapters, sendAction)
            HorizontalDivider()

            Viewer(state.viewer, sendAction)
            HorizontalDivider()

            Download(state.download, sendAction)
        }
    }
}

@Composable
private fun Main(main: Settings.Main, sendAction: (SettingsAction.SaveMain) -> Unit) {
    TogglePreferenceItem(
        title = R.string.dark_theme_title,
        subtitle = R.string.dark_theme_summary,
        icon = Icons.Default.DarkMode,
        initialValue = main.isDarkTheme,
        onCheckedChange = { sendAction(SettingsAction.SaveMain(main.copy(isDarkTheme = it))) }
    )

    HorizontalDivider()

    PreferenceTitle(R.string.library_title)

    TogglePreferenceItem(
        title = R.string.show_category_title,
        subtitle = R.string.show_category_summary,
        icon = Icons.Default.Category,
        initialValue = main.isShowCategory,
        onCheckedChange = { sendAction(SettingsAction.SaveMain(main.copy(isShowCategory = it))) }
    )
}

@Composable
private fun Chapters(chapters: Settings.Chapters, sendAction: (SettingsAction.SaveChapters) -> Unit) {
    PreferenceTitle(R.string.list_chapter_title)

    TogglePreferenceItem(
        title = R.string.list_chapter_filter_title,
        subtitle = R.string.list_chapter_filter_summary,
        icon = Icons.Default.FilterList,
        initialValue = chapters.isIndividual,
        onCheckedChange = { sendAction(SettingsAction.SaveChapters(chapters.copy(isIndividual = it))) }
    )

    TogglePreferenceItem(
        title = R.string.list_chapter_title_title,
        subtitle = R.string.list_chapter_title_summary,
        icon = Icons.Default.Title,
        initialValue = chapters.isTitle,
        onCheckedChange = { sendAction(SettingsAction.SaveChapters(chapters.copy(isTitle = it))) }
    )
}

@Composable
private fun Viewer(viewer: Settings.Viewer, sendAction: (SettingsAction.SaveViewer) -> Unit) {
    PreferenceTitle(R.string.viewer_title)

    ListPreferenceItem(
        title = R.string.viewer_orientation_title,
        subtitle = R.string.viewer_orientation_summary,
        icon = Icons.Default.CropLandscape,
        entries = R.array.viewer_orientation_array,
        entryValues = Orientation.entries,
        initialValue = viewer.orientation,
        onValueChange = { sendAction(SettingsAction.SaveViewer(viewer.copy(orientation = it))) }
    )

    MultiSelectListPreferenceItem(
        title = R.string.viewer_control_title,
        subtitle = R.string.viewer_control_summary,
        icon = Icons.Default.VideogameAsset,
        entries = R.array.viewer_control_array,
        initialValue = viewer.controls,
        onValueChange = {
            sendAction(SettingsAction.SaveViewer(viewer.copy(control = viewer.controls(it))))
        }
    )

    if (Build.VERSION.SDK_INT >= 28)
        TogglePreferenceItem(
            title = R.string.viewer_cutout_title,
            subtitle = R.string.viewer_cutout_summary,
            icon = Icons.Default.ContentCut,
            initialValue = viewer.cutOut,
            onCheckedChange = { sendAction(SettingsAction.SaveViewer(viewer.copy(cutOut = it))) }
        )

    TogglePreferenceItem(
        title = R.string.viewer_without_title,
        subtitle = R.string.viewer_without_summary,
        //            icon = Icons.Default.ContentCut,
        initialValue = viewer.withoutSaveFiles,
        onCheckedChange = { sendAction(SettingsAction.SaveViewer(viewer.copy(withoutSaveFiles = it))) }
    )

    TogglePreferenceItem(
        title = R.string.viewer_scrollbars_title,
        subtitle = R.string.viewer_scrollbars_summary,
        icon = Icons.Default.FitScreen,
        initialValue = viewer.useScrollbars,
        onCheckedChange = { sendAction(SettingsAction.SaveViewer(viewer.copy(useScrollbars = it))) }
    )

}

@Composable
private fun Download(download: Settings.Download, sendAction: (SettingsAction.SaveDownload) -> Unit) {
    PreferenceTitle(R.string.downloader_title)

    TogglePreferenceItem(
        title = R.string.downloader_parallel_title,
        subtitle = R.string.downloader_parallel_summary,
        icon = Icons.AutoMirrored.Filled.CompareArrows,
        initialValue = download.concurrent,
        onCheckedChange = { sendAction(SettingsAction.SaveDownload(download.copy(concurrent = it))) }
    )

    TogglePreferenceItem(
        title = R.string.downloader_retry_title,
        subtitle = R.string.downloader_retry_summary,
        icon = Icons.Default.ErrorOutline,
        initialValue = download.retry,
        onCheckedChange = { sendAction(SettingsAction.SaveDownload(download.copy(retry = it))) }
    )

    TogglePreferenceItem(
        title = R.string.downloader_wifi_only_title,
        subtitle = R.string.downloader_wifi_only_summary,
        icon = Icons.Default.Wifi,
        initialValue = download.wifi,
        onCheckedChange = { sendAction(SettingsAction.SaveDownload(download.copy(wifi = it))) }
    )
}
