package com.san.kir.features.accounts.shikimori.logic.useCases

import com.san.kir.features.accounts.shikimori.logic.fuzzy
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem
import com.san.kir.features.accounts.shikimori.logic.repo.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class BindingUseCase(private val itemsRepository: ItemsRepository) {
    // кеширование данных для ускорения проверки
    private var repositoryItems: List<MangaItem> = emptyList()

    fun <T : MangaItem> prepareData(): suspend (List<T>) -> List<BindStatus<T>> =
        { list ->
            initRepositoryItems()

            list.filter { checkNoBind(it) }
                .sortedBy { item -> item.name }
                .map { item -> BindStatus(item, CanBind.Check) }
        }

    fun <T : MangaItem> checkBinding(): suspend (List<BindStatus<T>>) -> Flow<CheckingStatus<T>> =
        { list ->
            flow {
                initRepositoryItems()

                val mutList = list.toMutableList()

                repeat(list.size) { index ->
                    val item = mutList[index]

                    val fuzzyCount = repositoryItems
                        .filter { checkNoBind(item.item) }
                        .map { manga2 -> item.item fuzzy manga2 }
                        .any { fuzzy -> fuzzy.second }

                    mutList[index] = item.copy(status = if (fuzzyCount) CanBind.Ok else CanBind.No)

                    emit(CheckingStatus(items = null, progress = index.toFloat() / list.size))
                }

                emit(
                    CheckingStatus(
                        items = mutList.sortedBy { (_, can) -> can.ordinal },
                        progress = null
                    )
                )
            }
        }

    fun <T : MangaItem> filterData(): suspend (List<T>) -> List<T> =
        { list ->
            if (list.isEmpty()) {
                list
            } else {
                initRepositoryItems()

                list.filterNot(::checkNoBind)
                    .sortedBy { item -> item.name }
            }
        }

    private suspend fun initRepositoryItems() {
        if (repositoryItems.isEmpty())
            repositoryItems = itemsRepository.items()
    }

    private fun checkNoBind(item: MangaItem): Boolean {
        return when (item) {
            is AccountMangaItem -> !item.inLibrary
            is LibraryMangaItem ->
                repositoryItems.firstOrNull { (it as AccountMangaItem).idInLibrary == item.id } == null

            else -> false
        }
    }
}

internal data class BindStatus<T : MangaItem>(
    val item: T,
    val status: CanBind
)

internal data class CheckingStatus<T : MangaItem>(
    val items: List<BindStatus<T>>?,
    val progress: Float?,
)

internal enum class CanBind { Already, Ok, No, Check }
