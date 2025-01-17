package com.san.kir.features.shikimori.ui.accountItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.rememberImage
import com.san.kir.features.shikimori.R
import com.san.kir.features.shikimori.logic.api.ShikimoriData
import com.san.kir.features.shikimori.ui.util.LogOutDialog
import com.san.kir.features.shikimori.ui.util.TextLoginOrNot

@Composable
fun AccountItem(navigateToManager: () -> Unit) {
    val viewModel: AccountItemViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LoginOrNot(
        state = state.login,
        navigateToManager = {
            when (state.login) {
                LoginState.LogOut, LoginState.Error -> viewModel.sendEvent(AccountItemEvent.LogIn)
                is LoginState.LogInOk               -> navigateToManager()
                else                                -> {}
            }
        },
        login = { viewModel.sendEvent(AccountItemEvent.LogIn) },
        logout = { viewModel.sendEvent(AccountItemEvent.LogOut) }
    )

    LogOutDialog(
        state = state.dialog,
        onDismiss = { viewModel.sendEvent(AccountItemEvent.CancelLogOut) },
        onConfirm = { viewModel.sendEvent(AccountItemEvent.LogOut) }
    )
}

@Composable
private fun LoginOrNot(
    state: LoginState,
    navigateToManager: () -> Unit,
    login: () -> Unit,
    logout: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = navigateToManager)
            .padding(vertical = Dimensions.quarter, horizontal = Dimensions.default)
            .horizontalInsetsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            rememberImage(ShikimoriData.iconUrl),
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
                Text(stringResource(R.string.whoami_error), color = MaterialTheme.colors.error)
            }
        }

        Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            FromEndToEndAnimContent(targetState = state) { targetState ->
                when (targetState) {
                    is LoginState.LogInError, is LoginState.LogInOk -> {
                        IconButton(onClick = logout) {
                            Icon(
                                Icons.Default.Logout, "",
                                modifier = Modifier.size(Dimensions.Image.small)
                            )
                        }
                    }

                    LoginState.LogOut                               -> {
                        IconButton(onClick = login) {
                            Icon(
                                Icons.Default.Login, "",
                                modifier = Modifier.size(Dimensions.Image.small)
                            )
                        }
                    }

                    LoginState.Error                                -> Icon(Icons.Default.Error, "")

                    is LoginState.LogInCheck, LoginState.Loading    -> CircularProgressIndicator()
                }
            }
        }
    }
}


