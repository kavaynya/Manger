package com.san.kir.schedule.ui.updates

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.custom.DbMinimalTaskManga



internal data class UpdatesState(
    val items: List<MiniManga>
) : ScreenState
