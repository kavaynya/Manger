package com.san.kir.catalog.ui.addOnline

import com.san.kir.core.utils.viewModel.ScreenState


internal data class AddOnlineState(
    val isCheckingUrl: Boolean = false,
    val validatesCatalogs: List<String>,
    val isErrorAvailable: Boolean = false,
    val isEnableAdding: Boolean = false,
) : ScreenState
