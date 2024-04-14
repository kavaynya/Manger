package com.san.kir.data.models.main

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Statistic(
    val id: Long = 0L,
    val mangaId: Long = 0L,
    val allChapters: Int = 0,
    val lastChapters: Int = 0,
    val allPages: Int = 0,
    val lastPages: Int = 0,
    val allTime: Long = 0L,
    val lastTime: Long = 0L,
    val maxSpeed: Int = 0,
    val downloadSize: Long = 0L,
    val lastDownloadSize: Long = 0L,
    val downloadTime: Long = 0L,
    val lastDownloadTime: Long = 0L,
    val openedTimes: Int = 0,
) : Parcelable
