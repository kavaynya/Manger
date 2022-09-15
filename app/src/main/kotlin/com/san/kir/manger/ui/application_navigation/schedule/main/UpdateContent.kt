package com.san.kir.manger.ui.application_navigation.schedule.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san.kir.data.models.extend.MiniManga
import com.san.kir.manger.R

@Composable
fun UpdateContent(
    viewModel: UpdateViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ) {
        items(items.size, { index -> items[index].id }) { index ->
            ItemContent(items[index], viewModel)
        }
    }
}

@Composable
private fun ItemContent(item: MiniManga, viewModel: UpdateViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, maxLines = 1)
            Text(
                stringResource(R.string.available_update_category_name, item.category),
                maxLines = 1,
                style = MaterialTheme.typography.subtitle2
            )
        }
        Switch(checked = item.update, onCheckedChange = {
            viewModel.update(item, it)
        })
    }
}

