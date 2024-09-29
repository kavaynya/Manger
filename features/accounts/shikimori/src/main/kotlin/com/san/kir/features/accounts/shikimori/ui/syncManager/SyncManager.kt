package com.san.kir.features.accounts.shikimori.ui.syncManager

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.db.main.repo.ChapterRepository
import com.san.kir.features.accounts.shikimori.logic.fuzzy
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriRate
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriStatus
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.accounts.shikimori.logic.repo.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber


internal interface ISyncManager<T : MangaItem> {
    val state: StateFlow<SyncState>
    suspend fun sendAction(action: Action)
    suspend fun checkSync(list: List<T> = emptyList())
}

internal suspend fun <T : MangaItem> ISyncManager<T>.checkSync(t: T): Unit = checkSync(listOf(t))

internal class SyncManager<T : MangaItem>(
    private val accountItemRepository: AccountItemRepository,
    private val chapterRepository: ChapterRepository,
    private val oppositeItemsRepository: ItemsRepository,
) : ISyncManager<T> {

    override val state = MutableStateFlow<SyncState>(SyncState.Finding)
    private val allItems = hashMapOf<Long, SyncItemState>()

    override suspend fun sendAction(action: Action) {
        when (action) {
            is SyncManagerAction.ApplySync -> applySync(action.itemId)
            is SyncManagerAction.Update -> update(action.item)
            is SyncManagerAction.Hide -> hide(action.itemId)
            is SyncManagerAction.CancelSync -> cancelSync(action.item)
        }
    }

    override suspend fun checkSync(list: List<T>) {
        Timber.v("launchSyncCheck")
        state.value = SyncState.Finding

        val checkingItems = list.ifEmpty { allItems.map { it.value.top } }

        when {
            checkingItems.isEmpty() -> state.value = SyncState.Empty
            checkingItems.size == 1 -> handleSingleCheckingItem(checkingItems.single())
            else -> handleMultipleCheckingItems(checkingItems)
        }
    }

    private suspend fun handleSingleCheckingItem(item: MangaItem) {
        if (hasBinding(item)) {
            val itemState = searchingSuitableItem(item)
            if (itemState == null) {
                state.value = SyncState.NotFound(item.name)
            } else {
                update(itemState)
            }
            return
        }

        val boundedMangaItem = when (item) {
            is AccountMangaItem -> oppositeItemsRepository.itemById(item.idInLibrary)
            is LibraryMangaItem -> oppositeItemsRepository.itemById(item.id)
            else -> null
        }

        state.value =
            if (boundedMangaItem == null) SyncState.Error else SyncState.Ok(boundedMangaItem)
    }

    private suspend fun handleMultipleCheckingItems(list: List<MangaItem>) {
        val checkingItems = list.filter { item -> hasBinding(item) }
        val availableItemStates = checkingItems.mapNotNull { item -> searchingSuitableItem(item) }

        if (availableItemStates.isEmpty()) {
            state.value = SyncState.Empty
        } else {
            update(*availableItemStates.toTypedArray())
        }
    }

    private suspend fun applySync(itemId: Long) {
        val itemState = allItems[itemId] ?: return

        accountItemRepository.bindItem(itemState.account.idInAccount, itemState.library.id)

        when (itemState.from) {
            SyncItemState.From.Account -> accountItemRepository.update(
                itemState.account.copy(read = itemState.selectedRead)
            )

            SyncItemState.From.Library -> chapterRepository.setReadFirst(
                itemState.library.id, itemState.library.sort, itemState.selectedRead
            )
        }

        checkSync()
    }

    private fun update(vararg syncItems: SyncItemState) {
        if (syncItems.isNotEmpty()) {
            syncItems.forEach { syncItemState ->
                allItems[syncItemState.id] = syncItemState
            }
        }
        state.value = SyncState.Founds(allItems.map { it.value })
    }

    private fun hide(itemId: Long) {
        allItems.remove(itemId)
        update()
    }

    private suspend fun cancelSync(mangaItem: MangaItem) {
        state.value = SyncState.Finding
        Timber.i("cancelSync\nprofileRate is $mangaItem")

        when (mangaItem) {
            is LibraryMangaItem -> accountItemRepository.unbindItem(mangaItem.id)

            is AccountMangaItem -> {
                accountItemRepository.unbindItem(mangaItem.idInLibrary)
                accountItemRepository.update(mangaItem.copy(status = ShikimoriStatus.OnHold))
            }
        }

        checkSync()
    }

    private suspend fun getSyncedItem(id: Long): SyncState {
        Timber.v("getSyncedItem with id $id")
        val manga = oppositeItemsRepository.itemById(id)

        return if (manga != null) {
            Timber.v("getSyncedItem finished with $manga")
            SyncState.Ok(manga)
        } else {
            SyncState.Error
        }
    }

    private suspend fun searchingSuitableItem(manga: MangaItem): SyncItemState? {
        Timber.v("searchForSync with $manga")
        val items = oppositeItemsRepository.items()

        val filtered = items
            .map { manga2 ->
                val fuzzy = manga fuzzy manga2
                fuzzy to manga2
            }
            .filter { (fuzzy, _) -> fuzzy.second }

        Timber.v("filtered is $filtered")

        if (filtered.isEmpty()) return null

        val sortedItems = filtered
            .sortedBy { (fuzzy, _) -> fuzzy.first }
            .map { (_, manga2) -> manga2 }

        return SyncItemState(manga, sortedItems)
    }

    private suspend fun hasBinding(mangaItem: MangaItem): Boolean {
        return when (mangaItem) {
            is AccountMangaItem -> mangaItem.inLibrary
            is LibraryMangaItem -> oppositeItemsRepository.itemById(mangaItem.id) != null
            else -> false
        }
    }

}

internal sealed interface SyncDialogState {
    data object None : SyncDialogState
    data class Init(val manga: MangaItem) : SyncDialogState

    data class DifferentChapterCount(
        val manga: MangaItem,
        val profileRate: ShikimoriRate,
        val local: Int,
        val online: Int
    ) : SyncDialogState

    data class DifferentReadCount(
        val manga: MangaItem,
        val profileRate: ShikimoriRate,
        val local: Int,
        val online: Int,
    ) : SyncDialogState

    data class CancelSync(val rate: ShikimoriRate) : SyncDialogState
}
