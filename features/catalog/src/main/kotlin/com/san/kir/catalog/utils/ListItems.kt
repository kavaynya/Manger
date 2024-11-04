package com.san.kir.catalog.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.CircleLogo
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.data.models.catalog.MiniCatalogItem

@Composable
internal fun ListItem(
    item: MiniCatalogItem,
    secondName: String,
    toAdd: (params: SharedParams) -> Unit,
    toInfo: (params: SharedParams) -> Unit,
    updateItem: (MiniCatalogItem) -> Unit,
) {
    val params = rememberSharedParams()
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .saveParams(params)
            .clickable { toInfo(params) }
            .horizontalInsetsPadding(horizontal = Dimensions.half, vertical = Dimensions.quarter),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.logo.isNotEmpty()) {
            CircleLogo(item.logo, size = Dimensions.Image.mini)
        }
        HalfSpacer()

        Column(
            Modifier
                .padding(end = Dimensions.default)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (secondName.isNotEmpty()) {
                Text(
                    text = secondName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        when (item.state) {
            MiniCatalogItem.State.Added -> {
                val params = rememberSharedParams(fromCenter = true)
                Image(
                    imageVector = Icons.Default.Add, "",
                    colorFilter = ColorFilter.tint(Color.Green),
                    modifier = Modifier
                        .size(Dimensions.Image.small)
                        .saveParams(params)
                        .clickable { toAdd(params) }
                )
            }

            MiniCatalogItem.State.Update ->
                Image(
                    imageVector = Icons.Default.Sync, "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .size(Dimensions.Image.small)
                        .clickable(onClick = { updateItem(item) })
                )

            MiniCatalogItem.State.None -> {
            }
        }
    }
}
