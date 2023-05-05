package com.san.kir.core.utils

import android.app.Application
import android.content.Context

object ManualDI {
    private var app: Application? = null

    fun init(app: Application) {
        this.app = app
    }

    val context: Context
        get() = checkNotNull(app) { "call init before use" }

    val application: Application
        get() = checkNotNull(app) { "call init before use" }
}
