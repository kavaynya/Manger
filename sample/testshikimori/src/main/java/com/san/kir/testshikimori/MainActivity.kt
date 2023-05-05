package com.san.kir.testshikimori

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.san.kir.core.utils.ManualDI
import com.san.kir.features.shikimori.ui.setContent

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ManualDI.init(this)
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent()
    }
}
