package com.san.kir.core.utils

fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
