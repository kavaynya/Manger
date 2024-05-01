package com.san.kir.data.models.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.ui.graphics.vector.ImageVector
import com.san.kir.data.models.R

enum class MainMenuType {
    Library {
        override fun stringId() = R.string.library
        override val icon: ImageVector
            get() = Icons.Default.LocalLibrary
    },
    Storage {
        override fun stringId() = R.string.storage
        override val icon: ImageVector
            get() = Icons.Default.Source
    },
    Category {
        override fun stringId() = R.string.categories
        override val icon: ImageVector
            get() = Icons.Filled.Category
    },
    Catalogs {
        override fun stringId() = R.string.catalogs
        override val icon: ImageVector
            get() = Icons.AutoMirrored.Filled.FormatListBulleted
    },
    Downloader {
        override fun stringId() = R.string.downloader
        override val icon: ImageVector
            get() = Icons.Default.Download
    },
    Latest {
        override fun stringId() = R.string.latest
        override val icon: ImageVector
            get() = Icons.Default.History
    },
    Settings {
        override fun stringId() = R.string.settings
        override val icon: ImageVector
            get() = Icons.Filled.Settings
    },
    Schedule {
        override fun stringId() = R.string.schedule
        override val icon: ImageVector
            get() = Icons.Filled.Schedule
    },
    Statistic {
        override fun stringId() = R.string.statistic
        override val icon: ImageVector
            get() = Icons.Default.QueryStats
    },
    Accounts {
        override fun stringId() = R.string.accounts
        override val icon: ImageVector
            get() = Icons.Default.People
    },
    Default {
        override fun stringId() = R.string.storage
        override val icon: ImageVector
            get() = Icons.Filled.Schedule
        override val added = false
    };

    abstract fun stringId(): Int
    abstract val icon: ImageVector
    open val added: Boolean = true
}
