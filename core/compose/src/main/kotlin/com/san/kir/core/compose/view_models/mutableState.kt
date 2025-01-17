package com.san.kir.core.compose.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.san.kir.core.compose.runtime.CustomMutableStateDelegate
import kotlin.properties.PropertyDelegateProvider

fun <T> ViewModel.mutableStateOf(
    defaultValue: T,
    onChange: suspend (T) -> Unit,
) = PropertyDelegateProvider<Any, CustomMutableStateDelegate<T>> { _, _ ->
    CustomMutableStateDelegate(
        scope = this.viewModelScope,
        defaultValue = defaultValue,
        onChange = onChange
    )
}
