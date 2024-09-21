package com.san.kir.features.shikimori.logic.repo

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.repo.AccountRepository
import com.san.kir.data.db.main.repo.AccountsMangaRepository
import com.san.kir.data.models.main.AccountManga
import com.san.kir.features.shikimori.logic.GraphQLQueries
import com.san.kir.features.shikimori.logic.api.ShikimoriApi
import com.san.kir.features.shikimori.logic.di.InternetClient
import com.san.kir.features.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.shikimori.logic.models.Auth
import com.san.kir.features.shikimori.logic.models.GraphQLResult
import com.san.kir.features.shikimori.logic.models.IdContainer
import com.san.kir.features.shikimori.logic.models.ShikimoriManga
import com.san.kir.features.shikimori.logic.models.ShikimoriRate
import com.san.kir.features.shikimori.logic.models.data
import com.san.kir.features.shikimori.logic.models.toMangaItem
import com.san.kir.features.shikimori.logic.models.toMangaItems
import com.san.kir.features.shikimori.logic.models.update
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


internal class AccountItemRepository(
    private val accountId: Long,
    private val accountRepository: AccountRepository,
    private val accountsMangaRepository: AccountsMangaRepository,
    private val client: InternetClient,
) : ItemsRepository {

    companion object {
        private const val TAG = "AccountItemRepository"
    }

    private val authData = accountRepository
        .loadItem(accountId)
        .map { ManualDI.stringToJson<Auth>(it?.data ?: "") }
        .filterNotNull()
        .onEach { client.updateTokens(it.tokenContainer) }

    private suspend fun currentAuthData(): Auth = authData.first()

    init {
        client.onTokenUpdate { newContainer ->
            val currentAuthData = currentAuthData().copy(tokenContainer = newContainer)
            accountRepository.update(accountId, ManualDI.jsonToString(currentAuthData))
        }
    }

    override fun loadItems() = accountsMangaRepository.loadItems(accountId).toMangaItems()

    override fun loadItemById(id: Long) =
        accountsMangaRepository.loadItemByIdInAccount(accountId, id).map { it?.toMangaItem() }

    override suspend fun items() = loadItems().first()

    override suspend fun itemById(libId: Long) =
        withIoContext { accountsMangaRepository.itemByIdInLibrary(accountId, libId)?.toMangaItem() }

    suspend fun loadItemByMangaId(idInSite: Long): Flow<AccountMangaItem?> {
        val dbItem = accountsMangaRepository.itemByIdInSite(accountId, idInSite)
        return if (dbItem != null) {
            loadItemById(dbItem.targetId)
        } else {
            flow {
                val manga = client
                    .post(ShikimoriApi.Graphql(), GraphQLQueries.mangaById(idInSite))
                    .body<GraphQLResult>()
                    .data
                    .manga
                    .firstOrNull()
                    ?.toMangaItem(accountId)
                emit(manga)
            }
        }
    }

    suspend fun bindItem(idInAccount: Long, idInLibrary: Long) = withIoContext {
        Timber.i("bindItem\nidInAccount is $idInAccount\nidInLibrary is $idInLibrary")
        accountsMangaRepository.setIdInLibrary(idInLibrary, accountId, idInAccount)
    }

    suspend fun unbindItem(idInAccount: Long) = withIoContext {
        accountsMangaRepository.resetIdInLibrary(accountId, idInAccount)
    }

    suspend fun refreshRates() {
        val currentItems = accountsMangaRepository.loadItems(accountId).first().map { it.targetId }

        rates().collect { list ->
            Timber.tag(TAG).w(list.toString())

            val insertItems = list.filter { rate ->
                rate.id != null && currentItems.contains(rate.id).not() && rate.manga?.id != null
            }.map { rate ->
                AccountManga(
                    accountId = accountId,
                    targetId = rate.id!!,
                    mangaId = rate.manga?.id!!,
                    data = ManualDI.jsonToString(rate)
                )
            }
            accountsMangaRepository.insert(insertItems)

            list.filter { rate ->
                rate.id != null && currentItems.contains(rate.id) && rate.manga?.id != null
            }.onEach { rate ->
                accountsMangaRepository.updateData(
                    accountId, rate.id!!, ManualDI.jsonToString(rate)
                )
            }
        }
    }

    suspend fun add(idInSite: Long): Long = withIoContext {
        val idInAccount = client
            .post(
                ShikimoriApi.V2.UserRates(),
                ShikimoriApi.Data.CreateRate(currentAuthData().user.id, idInSite)
            )
            .body<IdContainer.Simple>()
            .id

        if (idInAccount != null) {
            accountsMangaRepository.insert(accountId, idInAccount, idInSite)
            return@withIoContext refresh(0L, idInSite)
        }

        -1L
    }

    suspend fun refresh(idInAccount: Long, idInSite: Long): Long = withIoContext {
        if (idInAccount == -1L && idInSite == -1L) {
            return@withIoContext -1L
        }

        var idInSite = idInSite
        if (idInSite == -1L) {
            val dbItem =
                accountsMangaRepository.loadItemByIdInAccount(accountId, idInAccount).first()
            idInSite = dbItem!!.mangaId
        }

        val item = client.post(ShikimoriApi.Graphql(), GraphQLQueries.mangaById(idInSite))
            .body<GraphQLResult>()
            .data
            .manga.firstOrNull()!!.toMangaItem(accountId)

        accountsMangaRepository.updateData(accountId, item.idInAccount, item.data())
        item.idInAccount
    }

    suspend fun update(item: AccountMangaItem) = withIoContext {
        kotlin.runCatching {
            val newRate: ShikimoriRate =
                client.put(
                    ShikimoriApi.V2.UserRates.Id(id = item.idInAccount),
                    ShikimoriApi.Data.UpdateRate(
                        item.status, item.read, item.rewatches, item.userScore
                    )
                ).body()

            accountsMangaRepository.updateData(
                accountId, item.idInAccount, item.update(newRate).data()
            )
        }
    }

    suspend fun remove(idInAccount: Long) = withIoContext {
        kotlin.runCatching {
            client.delete(ShikimoriApi.V2.UserRates.Id(id = idInAccount))
            accountsMangaRepository.delete(accountId, idInAccount)
        }
    }

    suspend fun search(target: String): Result<List<ShikimoriManga>> = withIoContext {
        kotlin.runCatching {
            client
                .post(
                    ShikimoriApi.Graphql(),
                    GraphQLQueries.searchManga(target)
                )
                .body<GraphQLResult>()
                .data.manga
        }
    }

    private fun rates(): Flow<List<ShikimoriRate>> {
        return channelFlow {
            runCatching {
                var page = 1
                withIoContext {
                    while (true) {
                        val rates = client.post(
                            ShikimoriApi.Graphql(),
                            GraphQLQueries.userRateMangas(currentAuthData().user.id, page)
                        )
                            .body<GraphQLResult>()
                            .data
                            .userRates
                        if (rates.isNotEmpty()) send(rates) else break
                        page++
                    }
                }
            }.onFailure {
                Timber.tag(TAG).e(it)
            }
            close()
        }
    }
}
