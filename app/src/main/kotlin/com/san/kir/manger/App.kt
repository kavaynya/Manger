package com.san.kir.manger

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.san.kir.core.utils.ManualDI
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        ManualDI.init(this)
    }
}
