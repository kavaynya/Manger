package com.san.kir.schedule.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.endInsetsPadding
import com.san.kir.core.compose.startInsetsPadding

@Composable
internal fun ItemContent(
    title: String,
    subTitle: String,
    checked: Boolean,
    onClick: ((SharedParams) -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit,
) {
    val params = rememberSharedParams()
    Row(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke(params) }
            .fillMaxWidth()
            .saveParams(params)
            .padding(vertical = Dimensions.smaller, horizontal = Dimensions.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .startInsetsPadding()
        ) {
            Text(title, maxLines = 1)
            Text(
                subTitle,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle2
            )
        }
        Switch(
            checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.endInsetsPadding()
        )
    }
}
