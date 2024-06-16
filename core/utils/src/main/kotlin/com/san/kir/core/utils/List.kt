package com.san.kir.core.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private inline fun <T> List<T>.mutate(mutation: MutableList<T>.() -> Unit): List<T> {
    val mutableList = toMutableList()
    mutableList.mutation()
    return mutableList
}

public fun <T> List<T>.add(item: T): List<T> = mutate { add(item) }
public fun <T> List<T>.add(index: Int, item: T): List<T> = mutate { add(index, item) }
public fun <T> List<T>.addAll(items: Collection<T>): List<T> = mutate { addAll(items) }
public fun <T> List<T>.addAll(index: Int, items: Collection<T>): List<T> =
    mutate { addAll(index, items) }

public fun <T> List<T>.set(index: Int, item: T): List<T> = mutate { set(index, item) }
public fun <T> List<T>.removeAt(index: Int): List<T> = mutate { removeAt(index) }
public fun <T> MutableStateFlow<List<T>>.set(index: Int, item: T): Unit =
    update { it.set(index, item) }

public fun <T> MutableStateFlow<List<T>>.addAll(index: Int, items: Collection<T>): Unit =
    update { it.addAll(index, items) }

public fun <T> MutableStateFlow<List<T>>.add(item: T): Unit = update { it.add(item) }
public fun <T> MutableStateFlow<List<T>>.removeAt(index: Int): Unit = update { it.removeAt(index) }
public fun <T> MutableStateFlow<List<T>>.listMap(transform: (T) -> T): Unit =
    update { it.map(transform) }

public fun <T> StateFlow<List<T>>.get(index: Int): T = value[index]

public fun <T> StateFlow<List<T>>.indexOfFirst(predicate: (T) -> Boolean): Int =
    value.indexOfFirst(predicate)
