package com.san.kir.features.viewer.logic

import androidx.lifecycle.SavedStateHandle
import com.san.kir.data.models.main.Chapter

internal class SavedChapter(private val savedStateHandle: SavedStateHandle) {
    private val chapterKey = "chapterKetSaveState"

    fun get() = savedStateHandle.get<Chapter>(chapterKey)
    fun set(chapter: Chapter) = savedStateHandle.set(chapterKey, chapter)
    fun clear() = savedStateHandle.remove<Chapter>(chapterKey)
}
