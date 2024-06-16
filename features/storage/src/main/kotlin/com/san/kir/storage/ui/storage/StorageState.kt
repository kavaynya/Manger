package com.san.kir.storage.ui.storage

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.Storage
import kotlinx.parcelize.Parcelize

internal data class StorageState(
    val background: BackgroundState = BackgroundState.None,
    val mangaName: String = "",
    val item: Storage = Storage(),
    val size: Double = 0.0,
) : ScreenState

@Stable
internal sealed interface BackgroundState {
    data object None : BackgroundState
    data object Load : BackgroundState
    data object Deleting : BackgroundState
}

@Parcelize
internal enum class DeleteStatus : Parcelable {
    Read, All
}
