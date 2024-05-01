package com.san.kir.core.utils

private inline fun <K, V> Map<K, V>.mutate(mutation: MutableMap<K, V>.() -> Unit): Map<K, V> {
    val mutableMap = toMutableMap()
    mutableMap.mutation()
    return mutableMap
}

fun <K, V> Map<K, V>.put(key: K, value: V) = mutate { put(key, value) }
fun <K, V> Map<K, V>.remove(key: K) = mutate { remove(key) }
