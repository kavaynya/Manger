package com.san.kir.data.db.main.migrations

/*
Таблица downloads
создание
*/
internal val from20to21 = migrate {
    from = 20
    to = 21

    query(
        "CREATE TABLE IF NOT EXISTS downloads (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "link TEXT NOT NULL, " +
                "path TEXT NOT NULL, " +
                "totalPages INTEGER NOT NULL, " +
                "downloadPages INTEGER NOT NULL, " +
                "totalSize INTEGER NOT NULL, " +
                "downloadSize INTEGER NOT NULL, " +
                "totalTime INTEGER NOT NULL, " +
                "status INTEGER NOT NULL, " +
                "`order` INTEGER NOT NULL)"
    )
}

/*
Таблица downloads
пересоздание
*/
internal val from21to22 = migrate {
    from = 21
    to = 22

    query("DROP TABLE IF EXISTS downloads")
    query(
        "CREATE TABLE IF NOT EXISTS downloads (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "link TEXT NOT NULL, " +
                "path TEXT NOT NULL, " +
                "totalPages INTEGER NOT NULL, " +
                "downloadPages INTEGER NOT NULL, " +
                "totalSize INTEGER NOT NULL, " +
                "downloadSize INTEGER NOT NULL, " +
                "totalTime INTEGER NOT NULL, " +
                "status INTEGER NOT NULL, " +
                "`order` INTEGER NOT NULL)"
    )
}

/*
Таблица categories
Добавление полей spanPortrait, spanLandscape, isLargePortrait, isLargeLandscape
Используются для индивидуальной настройки отображения манги в библиотеке
*/
internal val from22to23 = migrate {
    from = 22
    to = 23

    query("ALTER TABLE categories RENAME TO tmp_categories")
    query(
        "CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "`order` INTEGER NOT NULL, " +
                "isVisible INTEGER NOT NULL, " +
                "typeSort TEXT NOT NULL, " +
                "isReverseSort INTEGER NOT NULL, " +
                "spanPortrait INTEGER NOT NULL DEFAULT 2, " +
                "spanLandscape INTEGER NOT NULL DEFAULT 3, " +
                "isListPortrait INTEGER NOT NULL DEFAULT 1, " +
                "isListLandscape INTEGER NOT NULL DEFAULT 1)"
    )
    query(
        "INSERT INTO `categories`(" +
                "`id`,`name`,`order`,`isVisible`,`typeSort`,`isReverseSort`) " +
                "SELECT " +
                "`id`,`name`,`order`,`isVisible`,`typeSort`,`isReverseSort` " +
                "FROM tmp_categories"
    )
    query(
        "DROP TABLE tmp_categories"
    )
}

/*
Таблица manga
Добавление полей populate, order
Используются для сортирвки в библиотеке
*/
internal val from23to24 = migrate {
    from = 23
    to = 24

    renameTableToTmp("manga")

    query(
        "CREATE TABLE `manga` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`unic` TEXT NOT NULL, " +
                "`host` TEXT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`authors` TEXT NOT NULL, " +
                "`logo` TEXT NOT NULL, " +
                "`about` TEXT NOT NULL, " +
                "`categories` TEXT NOT NULL, " +
                "`genres` TEXT NOT NULL, " +
                "`path` TEXT NOT NULL, " +
                "`status` TEXT NOT NULL, " +
                "`site` TEXT NOT NULL, " +
                "`color` INTEGER NOT NULL, " +
                "`populate` INTEGER NOT NULL DEFAULT 0, " +
                "`order` INTEGER NOT NULL DEFAULT 0)"
    )

    query(
        "INSERT INTO manga(" +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color) " +
                "SELECT " +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color " +
                "FROM $tmpTable"
    )

    removeTmpTable()
}

/*
Таблица manga
Добавление поля isAlternativeSort
Используется для альтернативной сортировки глав
*/
internal val from24to25 = migrate {
    from = 24
    to = 25

    renameTableToTmp("manga")

    query(
        "CREATE TABLE `manga` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`unic` TEXT NOT NULL, " +
                "`host` TEXT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`authors` TEXT NOT NULL, " +
                "`logo` TEXT NOT NULL, " +
                "`about` TEXT NOT NULL, " +
                "`categories` TEXT NOT NULL, " +
                "`genres` TEXT NOT NULL, " +
                "`path` TEXT NOT NULL, " +
                "`status` TEXT NOT NULL, " +
                "`site` TEXT NOT NULL, " +
                "`color` INTEGER NOT NULL, " +
                "`populate` INTEGER NOT NULL DEFAULT 0, " +
                "`order` INTEGER NOT NULL DEFAULT 0, " +
                "`isAlternativeSort` INTEGER NOT NULL DEFAULT 1)"
    )

    query(
        "INSERT INTO manga(" +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color, populate, `order`) " +
                "SELECT " +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color, populate, `order` " +
                "FROM $tmpTable"
    )

    removeTmpTable()
}

/*
Таблица downloads
Добавление поля manga
*/
internal val from25to26 = migrate {
    from = 25
    to = 26

    renameTableToTmp("downloads")

    query(
        "CREATE TABLE downloads (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "manga TEXT NOT NULL DEFAULT ``, " +
                "name TEXT NOT NULL, " +
                "link TEXT NOT NULL, " +
                "path TEXT NOT NULL, " +
                "totalPages INTEGER NOT NULL, " +
                "downloadPages INTEGER NOT NULL, " +
                "totalSize INTEGER NOT NULL, " +
                "downloadSize INTEGER NOT NULL, " +
                "totalTime INTEGER NOT NULL, " +
                "status INTEGER NOT NULL, " +
                "`order` INTEGER NOT NULL)"
    )

    query(
        "INSERT INTO downloads(" +
                "id, name, link,path, totalPages, downloadPages,totalSize, " +
                "downloadSize, totalTime,status, `order`) " +
                "SELECT " +
                "id, name, link,path, totalPages, downloadPages,totalSize, " +
                "downloadSize, totalTime,status, `order` " +
                "FROM $tmpTable"
    )

    removeTmpTable()
}

/*
Таблица manga
Добавление поля isUpdate
Используется для запрета обновления глав у манги
*/
internal val from26to27 = migrate {
    from = 26
    to = 27

    renameTableToTmp("manga")

    query(
        "CREATE TABLE `manga` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`unic` TEXT NOT NULL, " +
                "`host` TEXT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`authors` TEXT NOT NULL, " +
                "`logo` TEXT NOT NULL, " +
                "`about` TEXT NOT NULL, " +
                "`categories` TEXT NOT NULL, " +
                "`genres` TEXT NOT NULL, " +
                "`path` TEXT NOT NULL, " +
                "`status` TEXT NOT NULL, " +
                "`site` TEXT NOT NULL, " +
                "`color` INTEGER NOT NULL, " +
                "`populate` INTEGER NOT NULL DEFAULT 0, " +
                "`order` INTEGER NOT NULL DEFAULT 0, " +
                "`isAlternativeSort` INTEGER NOT NULL DEFAULT 1, " +
                "isUpdate INTEGER NOT NULL DEFAULT 1)"
    )

    query(
        "INSERT INTO manga(" +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color, populate, `order`, isAlternativeSort) " +
                "SELECT " +
                "id, unic, host, name, authors, logo, about, categories, genres, path, status, site, color, populate, `order`, isAlternativeSort " +
                "FROM $tmpTable"
    )

    removeTmpTable()
}

/*
Таблица planned_task
Создание
Используется для создания автоматических обновлений
*/
internal val from27to28 = migrate {
    from = 27
    to = 28

    query(
        "CREATE TABLE IF NOT EXISTS `planned_task` (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "manga TEXT NOT NULL, " +
                "group_name TEXT NOT NULL, " +
                "group_content TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "type INTEGER NOT NULL, " +
                "is_enabled INTEGER NOT NULL, " +
                "period INTEGER NOT NULL, " +
                "day_of_week INTEGER NOT NULL, " +
                "hour INTEGER NOT NULL, " +
                "minute INTEGER NOT NULL, " +
                "added_time INTEGER NOT NULL, " +
                "error_message TEXT NOT NULL)"
    )
}

/*
Таблица planned_task
Добавление поля catalog
Для обновления каталогов манги
*/
internal val from28to29 = migrate {
    from = 28
    to = 29

    renameTableToTmp("planned_task")

    query(
        "CREATE TABLE IF NOT EXISTS planned_task (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "manga TEXT NOT NULL, " +
                "group_name TEXT NOT NULL, " +
                "group_content TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "catalog TEXT NOT NULL DEFAULT ``, " +
                "type INTEGER NOT NULL, " +
                "is_enabled INTEGER NOT NULL, " +
                "period INTEGER NOT NULL, " +
                "day_of_week INTEGER NOT NULL, " +
                "hour INTEGER NOT NULL, " +
                "minute INTEGER NOT NULL, " +
                "added_time INTEGER NOT NULL, " +
                "error_message TEXT NOT NULL)"
    )

    query(
        "INSERT INTO planned_task(" +
                "id, manga, group_name, group_content, category, type, is_enabled, " +
                "period, day_of_week, hour, minute, added_time, error_message) " +
                "SELECT " +
                "id, manga, group_name, group_content, category, type, is_enabled, " +
                "period, day_of_week, hour, minute, added_time, error_message " +
                "FROM $tmpTable"
    )

    removeTmpTable()
}

/*
Таблица statistic
Создание
Сбор статистики чтения
*/
internal val from29to30 = migrate {
    from = 29
    to = 30

    query(
        "CREATE TABLE IF NOT EXISTS `statistic` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`manga` TEXT NOT NULL, " +
                "`all_chapters` INTEGER NOT NULL, " +
                "`last_chapters` INTEGER NOT NULL, " +
                "`all_pages` INTEGER NOT NULL, " +
                "`last_pages` INTEGER NOT NULL, " +
                "`all_time` INTEGER NOT NULL, " +
                "`last_time` INTEGER NOT NULL, " +
                "`max_speed` INTEGER NOT NULL, " +
                "`download_size` INTEGER NOT NULL, " +
                "`download_time` INTEGER NOT NULL, " +
                "`opened_times` INTEGER NOT NULL)"
    )
}
