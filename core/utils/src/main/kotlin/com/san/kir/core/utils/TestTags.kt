package com.san.kir.core.utils

public object TestTags {
    public object Library {
        private const val base = "Library_"
        public const val tab: String = base + "tab"
        public const val empty_view: String = base + "empty_view"
        public const val page: String = base + "page"
        public const val item: String = base + "item"

    }

    public object Drawer {
        private const val base = "Base_"
        public const val drawer_open: String = base + "drawer_open"
        public const val nav_back: String = base + "nav_back"
        public const val item: String = base + "item"

    }
}
