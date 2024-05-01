package com.san.kir.catalog.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.systemBarsHorizontalPadding
import com.san.kir.data.models.extend.MiniCatalogItem

@Composable
fun ListItem(
    item: MiniCatalogItem,
    secondName: String,
    toAdd: (link: String, params: SharedParams) -> Unit,
    toInfo: (link: String, params: SharedParams) -> Unit,
    updateItem: (MiniCatalogItem) -> Unit,
) {
    val params = rememberSharedParams()
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .saveParams(params)
            .clickable { toInfo(item.link, params) }
            .padding(vertical = Dimensions.quarter, horizontal = Dimensions.default)
            .padding(systemBarsHorizontalPadding())
    ) {
        Column(
            Modifier
                .padding(end = Dimensions.default)
                .weight(1f, true)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = secondName,
                style = MaterialTheme.typography.subtitle2
            )
        }

        when (item.state) {
            MiniCatalogItem.State.Added -> {
                val params = rememberSharedParams(fromCenter = true)
                Image(
                    imageVector = Icons.Default.Add, "",
                    colorFilter = ColorFilter.tint(Color.Green),
                    modifier = Modifier
                        .size(Dimensions.Image.small)
                        .align(Alignment.CenterVertically)
                        .saveParams(params)
                        .clickable { toAdd(item.link, params) }
                )
            }

            MiniCatalogItem.State.Update ->
                Image(
                    imageVector = Icons.Default.Update, "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .size(Dimensions.Image.small)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = { updateItem(item) })
                )

            MiniCatalogItem.State.None -> {
            }
        }
    }
}
