package com.san.kir.data.models.main

import android.os.Parcelable
import com.san.kir.data.models.utils.MainMenuType
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainMenuItem (
    val id: Long = 0L,
    val name: String = "",
    val isVisible: Boolean = true,
    val order: Int = 0,
    val type: MainMenuType = MainMenuType.Default,
) : Parcelable
