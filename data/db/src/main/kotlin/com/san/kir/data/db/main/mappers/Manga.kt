package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.custom.DbMinimalStorageManga
import com.san.kir.data.db.main.custom.DbMinimalTaskManga
import com.san.kir.data.db.main.entites.DbManga
import com.san.kir.data.db.main.views.ViewManga
import com.san.kir.data.db.main.views.ViewMangaWithChapterCounts
import com.san.kir.data.models.main.Manga
import com.san.kir.data.models.main.MangaLogo
import com.san.kir.data.models.main.MangaWithChaptersCount
import com.san.kir.data.models.main.MiniManga
import com.san.kir.data.models.main.SimplifiedManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbManga.toModel() = Manga(
    id, host, name, logo, about, categoryId, path, status, color, populate, order,
    isAlternativeSort, isUpdate, chapterFilter, isAlternativeSite, shortLink, authorsList,
    genresList, lastUpdateError
)

@JvmName("toMangaModels")
internal fun List<DbManga>.toModels() = map(DbManga::toModel)

@JvmName("toFlowMangaModel")
internal fun Flow<DbManga?>.toModel() = map { it?.toModel() }

@JvmName("toFlowMangaModels")
internal fun Flow<List<DbManga>>.toModels() = map { it.toModels() }


internal fun Manga.toEntity() = DbManga(
    id, host, name, logo, about, categoryId, path, status, color, populate, order,
    isAlternativeSort, isUpdate, chapterFilter, isAlternativeSite, shortLink, authorsList,
    genresList, lastUpdateError
)

@JvmName("toMangaEntities")
internal fun List<Manga>.toEntities() = map(Manga::toEntity)


internal fun ViewManga.toModel() =
    SimplifiedManga(id, name, logo, color, populate, categoryId, category, noRead, hasError)

@JvmName("toSimplifiedMangaModels")
internal fun List<ViewManga>.toModels() = map(ViewManga::toModel)

@JvmName("toFlowSimplifiedMangaModel")
internal fun Flow<ViewManga?>.toModel() = map { it?.toModel() }

@JvmName("toFlowSimplifiedMangaModels")
internal fun Flow<List<ViewManga>>.toModels() = map { it.toModels() }


internal fun DbMinimalTaskManga.toModel() = MiniManga(id, name, update, category)

@JvmName("toMiniMangaModels")
internal fun List<DbMinimalTaskManga>.toModels() = map(DbMinimalTaskManga::toModel)

@JvmName("toFlowMiniMangaModel")
internal fun Flow<DbMinimalTaskManga?>.toModel() = map { it?.toModel() }

@JvmName("toFlowMiniMangaModels")
internal fun Flow<List<DbMinimalTaskManga>>.toModels() = map { it.toModels() }


internal fun ViewMangaWithChapterCounts.toModel() =
    MangaWithChaptersCount(id, name, logo, description, sort, read, all)

@JvmName("toMangaWithChaptersCountModels")
internal fun List<ViewMangaWithChapterCounts>.toModels() = map(ViewMangaWithChapterCounts::toModel)

@JvmName("toFlowMangaWithChaptersCountModel")
internal fun Flow<ViewMangaWithChapterCounts?>.toModel() = map { it?.toModel() }

@JvmName("toFlowMangaWithChaptersCountModels")
internal fun Flow<List<ViewMangaWithChapterCounts>>.toModels() = map { it.toModels() }


internal fun DbMinimalStorageManga.toModel() = MangaLogo(id, name, logo, path)
