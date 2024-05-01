package com.san.kir.features.catalogs.allhen.ui.comx

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.EndAnimatedVisibility
import com.san.kir.core.compose.animation.FromBottomToBottomAnimContent
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.utils.findInGoogle
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.parsing.sites.ComX
import com.san.kir.features.catalogs.allhen.R
import com.san.kir.features.catalogs.allhen.ui.allhen.LoginState

@Composable
fun ComxItemScreen(navigateToScreen: (String) -> Unit) {
    val viewModel: ComxItemStateHolder = stateHolder { ComxItemViewModel() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendAction(ComxItemEvent.Update)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToScreen(ComX.HOST_NAME) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            rememberImage(findInGoogle(ComX.HOST_NAME)),
            contentDescription = "allhentai site icon",
            modifier = Modifier
                .padding(vertical = Dimensions.half, horizontal = Dimensions.default)
                .size(Dimensions.Image.default)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(ComX.SITE_NAME)

            FromBottomToBottomAnimContent(targetState = state.login) {
                when (it) {
                    LoginState.Error    -> {
                        Text(stringResource(R.string.error), color = MaterialTheme.colorScheme.error)
                    }
                    LoginState.Loading  -> {}
                    is LoginState.LogIn -> Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.login_text, it.nickName))
                        Image(
                            rememberImage(it.avatar),
                            contentDescription = "",
                            modifier = Modifier.padding(start = Dimensions.half)
                        )
                    }
                    LoginState.NonLogIn -> Text(stringResource(R.string.no_auth_text))
                }
            }
        }

        EndAnimatedVisibility(visible = state.login is LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(end = Dimensions.default))
        }
    }
}
