package com.san.kir.catalog.ui.catalogs

import com.san.kir.core.utils.viewModel.Action


internal sealed interface CatalogsEvent : Action {
    data object UpdateData : CatalogsEvent
    data object UpdateContent : CatalogsEvent
}
