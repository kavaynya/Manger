package com.san.kir.manger.ui.init

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.FromBottomToBottomAnimContent
import com.san.kir.core.compose.animation.FromTopToTopAnimContent
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.manger.R
import timber.log.Timber

@Composable
fun InitScreen(navigateToItem: () -> Unit) {
    val holder: InitStateHolder = stateHolder { InitViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val next = holder.next(navigateToItem)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimensions.default),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(3f)
        ) {
            Image(
                painterResource(R.mipmap.ic_launcher_foreground),
                "app icon",
                modifier = Modifier.size(230.dp),
            )
            CircularProgressIndicator(
                modifier = Modifier.size(169.dp),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = Dimensions.half
            )
        }

        FromBottomToBottomAnimContent(
            targetState = state,
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (it) {
                    InitState.Init -> Unit

                    InitState.Memory ->
                        MemoryPermission(next)

                    InitState.Notification ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            NotificationPermission(next)
                }
            }
        }
    }
}

@Composable
private fun MemoryPermission(onFinish: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
        MemoryPermissionBeforeR(onFinish)
    else
        MemoryPermissionR(onFinish)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MemoryPermissionBeforeR(onFinish: () -> Unit) {
    // Storage permission state
    val storagePermissionState = rememberPermissionState(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    when (val status = storagePermissionState.status) {
        is PermissionStatus.Denied -> {
            FromTopToTopAnimContent(targetState = status.shouldShowRationale) {
                when (it) {
                    true ->
                        Text(
                            stringResource(R.string.storage_permission_nonpermission),
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = TextAlign.Center
                        )

                    false ->
                        Text(
                            stringResource(R.string.storage_permission_reason),
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = TextAlign.Center
                        )
                }
            }

            DefaultSpacer()

            FromBottomToBottomAnimContent(targetState = status.shouldShowRationale) {
                when (it) {
                    true -> {
                        val context = LocalContext.current
                        val intent = remember {
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        }
                        Button(onClick = { context.startActivity(intent) }) {
                            Text(stringResource(R.string.main_permission_go_to_setting))
                        }
                    }

                    false ->
                        Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                            Text(stringResource(R.string.main_permission_request))
                        }
                }
            }
        }

        PermissionStatus.Granted -> {
            Timber.v("hasPermission")
            onFinish()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun MemoryPermissionR(onFinish: () -> Unit) {
    var permissionRequire by remember { mutableStateOf(Environment.isExternalStorageManager()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { permissionRequire = Environment.isExternalStorageManager() }
    )

    if (permissionRequire.not()) {
        val context = LocalContext.current
        val intent = remember {
            Intent().apply {
                action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                data = Uri.fromParts("package", context.packageName, null)
            }
        }

        Text(stringResource(R.string.storage_permission_reason), textAlign = TextAlign.Center)
        DefaultSpacer()
        Button(onClick = { launcher.launch(intent) }) {
            Text(stringResource(R.string.main_permission_go_to_setting))
        }
    } else {
        onFinish()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermission(onFinish: () -> Unit) {


    // Storage permission state
    val permissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )

    when (val status = permissionState.status) {
        is PermissionStatus.Denied -> {
            FromTopToTopAnimContent(targetState = status.shouldShowRationale) {
                when (it) {
                    true ->
                        Text(
                            stringResource(R.string.notificaton_permission_nonpermission),
                            textAlign = TextAlign.Center
                        )

                    false ->
                        Text(
                            stringResource(R.string.notificaton_permission_reason),
                            textAlign = TextAlign.Center
                        )
                }
            }

            DefaultSpacer()

            FromBottomToBottomAnimContent(targetState = status.shouldShowRationale) {
                when (it) {
                    true -> {
                        val context = LocalContext.current
                        val intent = remember {
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        }
                        Button(onClick = { context.startActivity(intent) }) {
                            Text(stringResource(R.string.main_permission_go_to_setting))
                        }
                    }

                    false ->
                        Button(onClick = { permissionState.launchPermissionRequest() }) {
                            Text(stringResource(R.string.main_permission_request))
                        }
                }
            }
        }

        PermissionStatus.Granted -> onFinish()
    }
}
