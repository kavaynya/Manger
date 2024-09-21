package com.san.kir.features.shikimori.ui.accountItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.features.shikimori.R
import com.san.kir.features.shikimori.logic.api.ShikimoriData
import com.san.kir.features.shikimori.ui.util.LogOutDialog
import com.san.kir.features.shikimori.ui.util.TextLoginOrNot

@Composable
internal fun AccountItem(navigateToManager: (SharedParams) -> Unit) {
    val holder: AccountItemStateHolder = stateHolder { AccountItemViewModel() }
    val state by holder.state.collectAsState()

    LoginOrNot(
        state = state.login,
        navigateToManager = {
            when (state.login) {
                LoginState.LogOut, LoginState.Error -> holder.sendAction(AccountItemEvent.LogIn)
                is LoginState.LogInOk -> navigateToManager(it)
                else -> {}
            }
        },
        login = { holder.sendAction(AccountItemEvent.LogIn) },
        logout = { holder.sendAction(AccountItemEvent.LogOut) }
    )

    LogOutDialog(
        state = state.dialog,
        onDismiss = { holder.sendAction(AccountItemEvent.CancelLogOut) },
        onConfirm = { holder.sendAction(AccountItemEvent.LogOut) }
    )
}

@Composable
private fun LoginOrNot(
    state: LoginState,
    navigateToManager: (SharedParams) -> Unit,
    login: () -> Unit,
    logout: () -> Unit,
) {
    val params = rememberSharedParams()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .saveParams(params)
            .clickable(onClick = { navigateToManager(params) })
            .padding(vertical = Dimensions.quarter, horizontal = Dimensions.default)
            .horizontalInsetsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            rememberImage(ShikimoriData.ICON_URL),
            contentDescription = "Shikimori site icon",
            modifier = Modifier
                .padding(vertical = Dimensions.half)
                .padding(end = Dimensions.default)
                .size(Dimensions.Image.default)
        )

        Column(modifier = Modifier.weight(1f, true)) {
            Text(stringResource(R.string.site_name))
            TextLoginOrNot(state)
            BottomAnimatedVisibility(visible = state is LoginState.LogInError) {
                Text(stringResource(R.string.whoami_error), color = MaterialTheme.colorScheme.error)
            }
        }

        Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            FromEndToEndAnimContent(targetState = state) { targetState ->
                when (targetState) {
                    is LoginState.LogInError, is LoginState.LogInOk -> {
                        IconButton(onClick = logout) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout, "",
                                modifier = Modifier.size(Dimensions.Image.small)
                            )
                        }
                    }

                    LoginState.LogOut -> {
                        IconButton(onClick = login) {
                            Icon(
                                Icons.AutoMirrored.Filled.Login, "",
                                modifier = Modifier.size(Dimensions.Image.small)
                            )
                        }
                    }

                    LoginState.Error -> Icon(Icons.Default.Error, "")

                    is LoginState.LogInCheck, LoginState.Loading -> CircularProgressIndicator()
                }
            }
        }
    }
}


