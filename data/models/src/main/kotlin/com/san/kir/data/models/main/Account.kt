package com.san.kir.data.models.main

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.models.utils.AccountType
import timber.log.Timber

data class Account(
    val id: Long = -1L,
    val type: AccountType = AccountType.None,
    val data: String = ""
)

inline fun <reified T> Account.data(): T? {
   return runCatching { ManualDI.stringToJson<T>(data) }
       .onFailure { Timber.tag(ManualDI.TAG).e(it) }
       .getOrNull()
}
