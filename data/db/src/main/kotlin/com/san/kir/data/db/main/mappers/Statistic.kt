package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbStatistic
import com.san.kir.data.db.main.views.ViewStatistic
import com.san.kir.data.models.main.SimplifiedStatistic
import com.san.kir.data.models.main.Statistic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbStatistic.toModel() = Statistic(
    id, mangaId, allChapters, lastChapters, allPages, lastPages, allTime, lastTime, maxSpeed,
    downloadSize, lastDownloadSize, downloadTime, lastDownloadTime, openedTimes
)

@JvmName("toStatisticModels")
internal fun List<DbStatistic>.toModels() = map(DbStatistic::toModel)

@JvmName("toFlowStatisticModel")
internal fun Flow<DbStatistic?>.toModel() = map { it?.toModel() }

@JvmName("toFlowStatisticModels")
internal fun Flow<List<DbStatistic>>.toModels() = map { it.toModels() }


internal fun Statistic.toEntity() = DbStatistic(
    id, mangaId, allChapters, lastChapters, allPages, lastPages, allTime, lastTime, maxSpeed,
    downloadSize, lastDownloadSize, downloadTime, lastDownloadTime, openedTimes
)


internal fun ViewStatistic.toModel() = SimplifiedStatistic(id, name, logo, allTime)
@JvmName("toSimplifiedStatisticModels")
internal fun List<ViewStatistic>.toModels() = map(ViewStatistic::toModel)

@JvmName("toFlowSimplifiedStatisticModels")
internal fun Flow<List<ViewStatistic>>.toModels() = map { it.toModels() }
