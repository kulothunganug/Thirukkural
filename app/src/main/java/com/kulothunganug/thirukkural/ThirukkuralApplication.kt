package com.kulothunganug.thirukkural

import android.app.Application
import com.kulothunganug.thirukkural.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ThirukkuralApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ThirukkuralApplication)
            modules(appModule)
        }
    }
}