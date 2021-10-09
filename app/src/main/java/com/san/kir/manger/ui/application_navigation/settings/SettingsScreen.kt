package com.san.kir.manger.ui.application_navigation.settings

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.san.kir.manger.R
import com.san.kir.manger.data.datastore.DownloadRepository
import com.san.kir.manger.data.datastore.MainRepository
import com.san.kir.manger.ui.utils.RadioGroup
import com.san.kir.manger.ui.utils.TopBarScreenContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun SettingsScreen(
    nav: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    TopBarScreenContent(
        navHostController = nav,
        title = stringResource(R.string.main_menu_settings),
        additionalPadding = 0.dp
    ) {
        val theme by viewModel.theme.collectAsState()
        TogglePreferenceItem(
            title = R.string.settings_app_dark_theme_title,
            subtitle = R.string.settings_app_dark_theme_summary,
            initialValue = theme,
            onCheckedChange = { viewModel.setTheme(it) }
        )
        Divider()

        val showCategory by viewModel.showCategory.collectAsState()
        TogglePreferenceItem(
            title = R.string.settings_library_show_category_title,
            subtitle = R.string.settings_library_show_category_summary,
            initialValue = viewModel.showCategory,
            onCheckedChange = { viewModel.showCategory = it }
        )
        Divider()

        ListPreferenceItem(
            title = R.string.settings_viewer_orientation_title,
            subtitle = R.string.settings_viewer_orientation_summary,
            entries = R.array.settings_viewer_orientation_array,
            entryValues = R.array.settings_viewer_orientation_values,
            initialValue = viewModel.orientation,
            onValueChange = { viewModel.orientation = it }
        )

        MultiSelectListPreferenceItem(
            title = R.string.settings_viewer_control_title,
            subtitle = R.string.settings_viewer_control_summary,
            entries = R.array.settings_viewer_control_array,
            value = viewModel.control,
        )

        TogglePreferenceItem(
            title = R.string.settings_viewer_cutout_title,
            subtitle = R.string.settings_viewer_cutout_summary,
            initialValue = viewModel.cutout,
            onCheckedChange = { viewModel.cutout = it }
        )

        Divider()

        val concurrent by viewModel.concurrent.collectAsState()
        TogglePreferenceItem(
            title = R.string.settings_downloader_parallel_title,
            subtitle = R.string.settings_downloader_parallel_summary,
            initialValue = concurrent,
            onCheckedChange = { viewModel.setConcurrent(it) }
        )

        val retry by viewModel.retry.collectAsState()
        TogglePreferenceItem(
            title = R.string.settings_downloader_retry_title,
            subtitle = R.string.settings_downloader_retry_summary,
            initialValue = retry,
            onCheckedChange = { viewModel.setRetry(it) }
        )

        val wifi by viewModel.wifi.collectAsState()
        TogglePreferenceItem(
            title = R.string.settings_downloader_wifi_only_title,
            subtitle = R.string.settings_downloader_wifi_only_summary,
            initialValue = wifi,
            onCheckedChange = { viewModel.setWifi(it) }
        )
    }
}

@Composable
fun ListPreferenceItem(
    title: Int,
    subtitle: Int,
    entries: Int,
    entryValues: Int,
    initialValue: String,
    onValueChange: (String) -> Unit,
) {
    var dialog by remember { mutableStateOf(false) }
    TemplatePreferenceItem(title = title, subtitle = subtitle) {
        dialog = true
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = {
                Text(stringResource(title))
            },
            text = {
                RadioGroup(
                    state = initialValue,
                    onSelected = {
                        onValueChange(it)
                        dialog = false
                    },
                    stateList = stringArrayResource(entryValues).toList(),
                    textList = stringArrayResource(entries).toList()
                )
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                        onClick = { dialog = false }) {
                        Text("CANCEL")
                    }
                }
            }
        )
    }
}

@Composable
fun MultiSelectListPreferenceItem(
    title: Int,
    subtitle: Int,
    entries: Int,
    value: MutableList<Boolean>,
) {
    var dialog by remember { mutableStateOf(false) }
    TemplatePreferenceItem(title = title, subtitle = subtitle) {
        dialog = true
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = {
                Text(stringResource(title))
            },
            text = {
                val textList = stringArrayResource(entries).toList()

                Column {
                    textList.forEachIndexed { index, text ->
                        CheckBoxText(
                            state = value[index],
                            onChange = { value[index] = it },
                            firstText = text
                        )
                    }
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                        onClick = { dialog = false }) {
                        Text("CLOSE")
                    }
                }
            }
        )
    }
}


@Composable
fun TogglePreferenceItem(
    title: Int,
    subtitle: Int,
    initialValue: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {

    TemplatePreferenceItem(
        title = title, subtitle = subtitle,
        action = {
            Switch(
                checked = initialValue,
                onCheckedChange = { onCheckedChange(it) })
        }) {

    }
}

@Composable
fun TemplatePreferenceItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    title: Int,
    subtitle: Int,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Icon(icon, contentDescription = "")
                    }
                }
            }
            Column(
                modifier = Companion.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
                    Text(stringResource(title))
                }
                Spacer(modifier = Modifier.size(2.dp))
                ProvideTextStyle(value = MaterialTheme.typography.caption) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(stringResource(subtitle))
                    }
                }
            }
        }
        if (action != null)
            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(56.dp)
                    .width(1.dp),
            )
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (action != null)
                action()
        }
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    ctx: Application,
    private val main: MainRepository,
    private val download: DownloadRepository
) : ViewModel() {
    private val _theme = MutableStateFlow(true)
    val theme = _theme.asStateFlow()

    fun setTheme(value: Boolean) = viewModelScope.launch {
        main.setTheme(value)
    }

    private val _showCategory = MutableStateFlow(true)
    val showCategory = _showCategory.asStateFlow()

    fun setShowCategory(value: Boolean) = viewModelScope.launch {
        main.setShowCategory(value)
    }

    private val _concurrent = MutableStateFlow(true)
    val concurrent = _concurrent.asStateFlow()

    fun setConcurrent(value: Boolean) = viewModelScope.launch {
        download.setConcurrent(value)
    }

    private val _retry = MutableStateFlow(false)
    val retry = _retry.asStateFlow()

    fun setRetry(value: Boolean) = viewModelScope.launch {
        download.setRetry(value)
    }

    private val _wifi = MutableStateFlow(false)
    val wifi = _wifi.asStateFlow()

    fun setWifi(value: Boolean) = viewModelScope.launch {
        download.setWifi(value)
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            main.data
                .collect { data ->
                    _theme.update { data.theme }
                    _showCategory.update { data.isShowCatagery }
                }
        }

        viewModelScope.launch(Dispatchers.Default) {
            download.data
                .collect { data ->
                    _concurrent.update { data.concurrent }
                    _retry.update { data.retry }
                    _wifi.update { data.wifi }
                }
        }
    }
}
