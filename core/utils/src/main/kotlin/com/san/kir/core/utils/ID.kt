package com.san.kir.core.utils

object ID {
    private var count = 1

    fun generate(): Int {
        return count++
    }
}
