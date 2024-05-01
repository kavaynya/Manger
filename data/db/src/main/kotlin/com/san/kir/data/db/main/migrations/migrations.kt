package com.san.kir.data.db.main.migrations

import androidx.room.migration.Migration

internal val migrations: Array<Migration> = arrayOf(
    from9to10,  // Table Manga recreate
    from10to11, // Table Chapters recreate
    from11to12, // Table Categories recreate
    from12to13, // Table LatestChapters recreate
    from13to14, // Table MainMenuItems recreate
    from14to15, // Table Sites recreate

    from15to16, // Add StorageDir and StorageItem tables
    from16to17, // Empty
    from17to18, // For MainMenuItem table (add type field)
    from18to19, // For Sites table (add fields host, catalogName, volume, oldVolume, siteId) (remove count field)
    from19to20, // United tables StorageDir and StorageItem

    from20to21, // Table downloads create
    from21to22, // Table downloads recreate
    from22to23, // For categories table (add fields spanPortrait, spanLandscape, isLargePortrait, isLargeLandscape)
    from23to24, // For manga table (add fields populate, order)
    from24to25, // For manga table (add field isAlternativeSort)
    from25to26, // For downloads table (add field manga)
    from26to27, // For manga table (add field isUpdate)
    from27to28, // Table planned_task create
    from28to29, // For planned_task table (add field catalog)
    from29to30, // Table statistic create

    from30to31, // For chapters table (add field pages)
    from31to32, // For manga table (add field chapterFilter)
    from32to33, // For manga table (add field isAlternativeSite)
    from33to34, // For downloads table (add field error)
    from34to35, // For manga table (add field link)
    from35to36, // For chapters table (add field isInUpdate)
    from36to37, // For chapters table (add fields totalPages, downloadPages, totalSize, downloadSize, totalTime, status)
    from37to38, // For statistic table (add fields lastDownloadSize, lastDownloadTime)
    from38to39, // Delete downloads table, edit manga table, edit chapters table
    from39to40, // For Category table (order -> ordering)

    from40to41, // For Manga table (fill categoryId)
    from42to43, // For PlannedTask table (add & fill categoryId)
    from45to46, // remove LatestChapters table
    from49to50, // add manga_id field to Statistic table
    from50to51, // update manga_id field in Chapters table
    from54to55, // Update PlannedTask table (add & fill mangaId, remove category), remove PlannedTaskExt view
    from55to56, // Update PlannedTask table (add & fill mangaId, remove category), remove PlannedTaskExt view

    from61to62, // Update Settings table (add scrollbars field)
)
