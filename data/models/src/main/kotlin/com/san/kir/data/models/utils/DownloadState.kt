package com.san.kir.data.models.utils

import androidx.annotation.StringRes
import com.san.kir.data.models.R

enum class DownloadState(@StringRes val groupName: Int) {
    LOADING(R.string.loading),
    QUEUED(R.string.loading),
    PAUSED(R.string.paused),
    COMPLETED(R.string.completed),
    UNKNOWN(R.string.unknown),
    ERROR(R.string.error)
}
