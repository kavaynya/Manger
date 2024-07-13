package com.san.kir.catalog.ui.catalogs

import com.san.kir.core.utils.viewModel.Action


internal sealed interface CatalogsAction : Action {
    data object UpdateData : CatalogsAction
    data object UpdateContent : CatalogsAction
}
