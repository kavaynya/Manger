package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.Fonts
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriStatus
import com.san.kir.features.accounts.shikimori.ui.accountItem.LoginState

@Composable
internal fun StatusText(currentStatus: ShikimoriStatus?) {
    if (currentStatus != null) {
        val statuses = LocalContext.current.resources.getStringArray(R.array.statuses)
        Text(
            stringResource(R.string.current_status, statuses[currentStatus.ordinal]),
            fontSize = Fonts.Size.less,
        )
    }
}

@Composable
internal fun textLoginOrNot(state: LoginState): String {
    return when (state) {
        is LoginState.Ok, is LoginState.LogInCheck -> {
            stringResource(R.string.login_text, state.nickName)
        }

        LoginState.LogOut -> stringResource(R.string.no_login_text)
        LoginState.Error -> stringResource(R.string.error_try_again)
        else -> ""
    }
}

@Composable
internal fun ItemHeader(id: Int) {
    Text(
        stringResource(id),
        modifier = Modifier.fillMaxWidth().padding(Dimensions.half),
        textAlign = TextAlign.Center
    )
}

// Отображение названий манги с установленым стилем
@Composable
internal fun MangaNames(
    firstName: String = "",
    secondName: String = "",
) {
    ProvideTextStyle(Fonts.Style.bigBoldCenter) {
        if (firstName.isNotEmpty()) {
            Text(firstName, modifier = Modifier.fillMaxWidth())
        }

        if (secondName.isNotEmpty() && secondName != firstName) {
            Text(secondName, modifier = Modifier.fillMaxWidth())
        }
    }
}
