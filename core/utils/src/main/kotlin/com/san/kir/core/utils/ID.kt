package com.san.kir.core.utils

public object ID {
    private var count = 1

    public fun generate(): Int {
        return count++
    }
}
