package com.san.kir.data.models.utils

public enum class ChapterFilter {
    ALL_READ_ASC {
        override val isAll: Boolean = true
        override val isRead: Boolean = false
        override val isNot: Boolean = false
        override val isAsc: Boolean = true
        override fun toAll(): ChapterFilter = ALL_READ_ASC
        override fun toRead(): ChapterFilter = IS_READ_ASC
        override fun toNot(): ChapterFilter = NOT_READ_ASC
        override fun inverse(): ChapterFilter = ALL_READ_DESC
    },
    NOT_READ_ASC {
        override val isAll: Boolean = false
        override val isRead: Boolean = false
        override val isNot: Boolean = true
        override val isAsc: Boolean = true
        override fun toAll(): ChapterFilter = ALL_READ_ASC
        override fun toRead(): ChapterFilter = IS_READ_ASC
        override fun toNot(): ChapterFilter = NOT_READ_ASC
        override fun inverse(): ChapterFilter = NOT_READ_DESC
    },
    IS_READ_ASC {
        override val isAll: Boolean = false
        override val isRead: Boolean = true
        override val isNot: Boolean = false
        override val isAsc: Boolean = true
        override fun toAll(): ChapterFilter = ALL_READ_ASC
        override fun toRead(): ChapterFilter = IS_READ_ASC
        override fun toNot(): ChapterFilter = NOT_READ_ASC
        override fun inverse(): ChapterFilter = IS_READ_DESC
    },
    ALL_READ_DESC {
        override val isAll: Boolean = true
        override val isRead: Boolean = false
        override val isNot: Boolean = false
        override val isAsc: Boolean = false
        override fun toAll(): ChapterFilter = ALL_READ_DESC
        override fun toRead(): ChapterFilter = IS_READ_DESC
        override fun toNot(): ChapterFilter = NOT_READ_DESC
        override fun inverse(): ChapterFilter = ALL_READ_ASC
    },
    NOT_READ_DESC {
        override val isAll: Boolean = false
        override val isRead: Boolean = false
        override val isNot: Boolean = true
        override val isAsc: Boolean = false
        override fun toAll(): ChapterFilter = ALL_READ_DESC
        override fun toRead(): ChapterFilter = IS_READ_DESC
        override fun toNot(): ChapterFilter = NOT_READ_DESC
        override fun inverse(): ChapterFilter = NOT_READ_ASC
    },
    IS_READ_DESC {
        override val isAll: Boolean = false
        override val isRead: Boolean = true
        override val isNot: Boolean = false
        override val isAsc: Boolean = false
        override fun toAll(): ChapterFilter = ALL_READ_DESC
        override fun toRead(): ChapterFilter = IS_READ_DESC
        override fun toNot(): ChapterFilter = NOT_READ_DESC
        override fun inverse(): ChapterFilter = IS_READ_ASC
    };

    public abstract fun inverse(): ChapterFilter
    public abstract val isAll: Boolean
    public abstract val isRead: Boolean
    public abstract val isNot: Boolean
    public abstract val isAsc: Boolean
    public abstract fun toAll(): ChapterFilter
    public abstract fun toRead(): ChapterFilter
    public abstract fun toNot(): ChapterFilter
}
