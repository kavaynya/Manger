package com.san.kir.core.utils.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

public val mainDispatcher: MainCoroutineDispatcher = Dispatchers.Main

public val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

public val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
