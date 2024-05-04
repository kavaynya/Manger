package com.san.kir.data.models.main

import android.os.Parcelable
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.isEmptyDirectory
import com.san.kir.data.models.base.BaseChapter
import com.san.kir.data.models.utils.ChapterStatus
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.data.models.utils.preparePath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chapter(
    override val id: Long = 0,
    val mangaId: Long = 0,
    override val name: String = "",
    val date: String = "",
    private val _path: String = "",
    val isRead: Boolean = false,
    val link: String = "",
    val progress: Int = 0,
    override val pages: List<String> = listOf(),
    val isInUpdate: Boolean = false, // Пометка, что глав отображается в обновлениях
    override val downloadPages: Int = 0,
    val downloadSize: Long = 0L,
    val downloadTime: Long = 0L,
    val status: DownloadState = DownloadState.UNKNOWN,
    val order: Long = 0,
    override val addedTimestamp: Long = 0
) : Parcelable, BaseChapter {
    @IgnoredOnParcel
    override val path: String = _path.preparePath()
}

val Chapter.action: Int
    get() {  // Определение доступного действия для главы
        getFullPath(path).apply {
            when {
                // если ссылка есть и если папка пуста или папки нет, то можно скачать
                link.isNotEmpty() && (isEmptyDirectory || !exists()) -> return ChapterStatus.DOWNLOADABLE
                // если папка непустая, то статус соответствует удалению
                !isEmptyDirectory -> return ChapterStatus.DELETE
                // папка не существет и ссылки на загрузку нет, то больше ничего не сделаешь
                !exists() and link.isEmpty() -> return ChapterStatus.NOT_LOADED
            }
        }
        return ChapterStatus.UNKNOWN // такого быть не должно, но если случится дайте знать
    }
