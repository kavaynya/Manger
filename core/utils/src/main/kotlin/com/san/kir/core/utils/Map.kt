package com.san.kir.core.utils

private inline fun <K, V> Map<K, V>.mutate(mutation: MutableMap<K, V>.() -> Unit): Map<K, V> {
    val mutableMap = toMutableMap()
    mutableMap.mutation()
    return mutableMap
}

public fun <K, V> Map<K, V>.put(key: K, value: V): Map<K, V> = mutate { put(key, value) }
public fun <K, V> Map<K, V>.remove(key: K): Map<K, V> = mutate { remove(key) }
