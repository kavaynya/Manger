package com.san.kir.data.models.main

public data class SimplifiedManga(
    val id: Long = 0,
    val name: String = "",
    val logo: String = "",
    val color: Int = 0,
    val populate: Int = 0,
    val categoryId: Long = 0,
    val category: String = "",
    val noRead: Int = 0,
    val hasError: Boolean = false
)
