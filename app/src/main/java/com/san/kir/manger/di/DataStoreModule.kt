package com.san.kir.manger.di

import android.app.Application
import androidx.datastore.core.DataStore
import com.san.kir.manger.Chapters
import com.san.kir.manger.FirstLaunch
import com.san.kir.manger.Main
import com.san.kir.manger.data.datastore.chaptersStore
import com.san.kir.manger.data.datastore.firstLaunchStore
import com.san.kir.manger.data.datastore.mainStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    fun provideMainDataStore(application: Application): DataStore<Main> {
        return application.mainStore
    }

    @Provides
    fun provideChaptersDataStore(application: Application): DataStore<Chapters> {
        return application.chaptersStore
    }

    @Provides
    fun provideFirstLaunchDataStore(application: Application): DataStore<FirstLaunch> {
        return application.firstLaunchStore
    }
}
