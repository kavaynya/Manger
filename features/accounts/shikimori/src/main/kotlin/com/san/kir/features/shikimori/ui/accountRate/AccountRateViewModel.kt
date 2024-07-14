package com.san.kir.features.shikimori.ui.accountRate

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.ShikiDbManga
import com.san.kir.data.models.base.ShikimoriRate
import com.san.kir.features.shikimori.logic.SyncDialogEvent
import com.san.kir.features.shikimori.logic.SyncDialogState
import com.san.kir.features.shikimori.logic.SyncManager
import com.san.kir.features.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.shikimori.logic.di.profileItemRepository
import com.san.kir.features.shikimori.logic.di.settingsRepository
import com.san.kir.features.shikimori.logic.di.syncManager
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import com.san.kir.features.shikimori.logic.repo.SettingsRepository
import com.san.kir.features.shikimori.logic.useCases.SyncState
import com.san.kir.features.shikimori.logic.useCases.SyncUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class AccountRateViewModel(
    libraryItemRepository: LibraryItemRepository = ManualDI.libraryItemRepository,
    private val settingsRepository: SettingsRepository = ManualDI.settingsRepository,
    private val profileItemRepository: ProfileItemRepository = ManualDI.profileItemRepository,
    private val syncManager: SyncManager = ManualDI.syncManager,
) : ViewModel<AccountRateState>(), AccountRateStateHolder {
    private val syncCheck = SyncUseCase<ShikiDbManga>(libraryItemRepository)
    private val profileState = MutableStateFlow<ProfileState>(ProfileState.Load)
    private val mangaState = MutableStateFlow<MangaState>(MangaState.Load)

    init {
        profileState
            .filterIsInstance<ProfileState.Ok>()
            .mapNotNull { it.rate.targetId }
            .flatMapLatest(profileItemRepository::loadItemById)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { syncCheck.launchSyncCheck(it) }
            .launchIn(viewModelScope)

        syncManager.beforeBindChange { syncCheck.findingSyncCheck() }
        syncManager.onBindChange { syncCheck.launchSyncCheck() }
    }

    override val tempState = combine(
        syncCheck.syncState, syncManager.dialogState, profileState, mangaState, ::AccountRateState
    )

    override val defaultState = AccountRateState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            AccountRateEvent.ExistToggle -> {
                when (val profile = state.value.profile) {
                    ProfileState.Load -> {}
                    ProfileState.None -> updateStateInProfile { mangaId ->
                        Timber.v("add rate to profile")
                        profileItemRepository.add(
                            ShikimoriRate(
                                targetId = mangaId,
                                userId = settingsRepository.currentAuth().whoami.id,
                            )
                        ).onFailure(Timber::e)
                    }
                    is ProfileState.Ok -> updateStateInProfile {
                        profileItemRepository.remove(profile.rate)
                        syncCheck.cancelSyncCheck()
                    }
                }
            }
            is AccountRateEvent.Update -> {
                when {
                    event.id != null -> setId(event.id)
                    event.item != null ->
                        updateStateInProfile { profileItemRepository.update(event.item) }
                    else -> updateStateInProfile { }
                }
            }
            is AccountRateEvent.Sync -> onSyncEvent(event.event)
        }
    }

    private suspend fun onSyncEvent(event: SyncDialogEvent) {
        when (event) {
            SyncDialogEvent.DialogDismiss -> syncManager.dialogNone()
            is SyncDialogEvent.SyncCancel -> syncManager.cancelSync(event.rate)
            is SyncDialogEvent.SyncToggle ->
                when (state.value.sync) {
                    is SyncState.Founds -> syncManager.initSync(event.item)
                    is SyncState.Ok -> {
                        val profile = state.value.profile
                        if (profile is ProfileState.Ok)
                            syncManager.askCancelSync(profile.rate)
                    }
                    else -> {}
                }
            is SyncDialogEvent.SyncNext -> {
                when (val currentState = state.value.dialog) {
                    is SyncDialogState.Init -> {
                        val manga = state.value.manga
                        if (manga !is MangaState.Ok) return

                        val profile = state.value.profile
                        if (profile !is ProfileState.Ok) return

                        syncManager.checkAllChapters(
                            manga.item.chapters, profile.rate, currentState.manga
                        )
                    }
                    is SyncDialogState.DifferentChapterCount ->
                        syncManager.checkReadChapters(currentState.profileRate, currentState.manga)
                    is SyncDialogState.DifferentReadCount -> {
                        syncManager.launchSync(
                            currentState.profileRate, currentState.manga, event.onlineIsTruth
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    // Установка id текущего элемента и загрузка информации
    private suspend fun setId(mangaId: Long) {
        if (mangaId != -1L) {
            updateStateInProfile(mangaId) { id ->
                profileItemRepository
                    .manga(id)
                    .onSuccess { manga -> mangaState.update { MangaState.Ok(manga) } }
                    .onFailure {
                        mangaState.update { MangaState.Error }
                        Timber.e(it)
                    }
            }
        }
    }

    private suspend fun updateStateInProfile(
        id: Long? = null,
        action: suspend (Long) -> Unit,
    ) {
        profileState.update { ProfileState.Load }

        val manga = state.value.manga
        var rate: ShikimoriRate? = null

        when {
            id != null -> {
                action(id)
                profileItemRepository.rates(settingsRepository.currentAuth(), id)
                    .onSuccess { items -> rate = items.firstOrNull() }
            }
            manga is MangaState.Ok -> {
                action(manga.item.id)
                profileItemRepository.rates(settingsRepository.currentAuth(), manga.item.id)
                    .onSuccess { items -> rate = items.firstOrNull() }
            }
        }

        delay(1.seconds)

        profileState.update { rate?.let { ProfileState.Ok(it) } ?: ProfileState.None }
    }
}
