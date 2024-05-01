package com.san.kir.catalog.ui.catalogItem

import com.san.kir.catalog.logic.di.catalogRepository
import com.san.kir.catalog.logic.repo.CatalogRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.catalog.entities.DbSiteCatalogElement
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class CatalogItemViewModel(
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager,
    private val catalogRepository: CatalogRepository = ManualDI.catalogRepository,
) : ViewModel<CatalogItemState>(), CatalogItemStateHolder {
    private val item = MutableStateFlow(DbSiteCatalogElement())
    private val containingInLibrary =
        MutableStateFlow<ContainingInLibraryState>(ContainingInLibraryState.Check)
    private val background = MutableStateFlow<BackgroundState>(BackgroundState.Load)

    override val tempState = combine(item, containingInLibrary, background, ::CatalogItemState)

    override val defaultState = CatalogItemState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is CatalogItemEvent.Set -> {
                val item = manager.elementByUrl(event.url)
                if (item == null) {
                    background.update { BackgroundState.Error }
                    containingInLibrary.update { ContainingInLibraryState.Ok }
                } else {
                    this.item.update { item }
                    background.update { BackgroundState.None }

                    containingInLibrary.update {
                        if (catalogRepository.checkContains(item.shortLink))
                            ContainingInLibraryState.Ok
                        else
                            ContainingInLibraryState.None
                    }
                }
            }
        }
    }
}
