package com.san.kir.accounts.ui.catalogItem

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.accounts.R
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.EndAnimatedVisibility
import com.san.kir.core.compose.animation.FromBottomToBottomAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.utils.findInGoogle
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.parsing.SiteConstants

@Composable
internal fun CatalogItemScreen(
    constants: SiteConstants,
    navigateToScreen: (String, String, SharedParams) -> Unit,
) {
    val holder: CatalogItemStateHolder = stateHolder { bus -> CatalogItemViewModel(bus, constants) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()
    val params = rememberSharedParams()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .saveParams(params)
            .clickable { navigateToScreen(constants.AUTH_URL, constants.SITE_NAME, params) }
            .padding(vertical = Dimensions.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            rememberImage(findInGoogle(constants.HOST_NAME)),
            contentDescription = "site icon",
            modifier = Modifier
                .padding(vertical = Dimensions.half, horizontal = Dimensions.default)
                .size(Dimensions.Image.default)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(constants.SITE_NAME)

            FromBottomToBottomAnimContent(targetState = state.login) {
                when (it) {
                    LoginState.Error -> {
                        Text(
                            stringResource(R.string.error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    LoginState.Loading -> {}
                    is LoginState.LogIn ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.login_text, it.nickName))
                            Image(
                                rememberImage(it.avatar),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(Dimensions.Image.mini)
                                    .padding(start = Dimensions.half)
                            )
                        }

                    LoginState.NonLogIn -> Text(stringResource(R.string.no_auth_text))
                }
            }
        }

        EndAnimatedVisibility(visible = state.login is LoginState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = Dimensions.default)
                    .size(Dimensions.ProgressBar.big)
            )
        }
    }
}
