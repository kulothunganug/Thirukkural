package com.kulothunganug.thirukkural

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kulothunganug.thirukkural.di.appModule
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val manager = GlanceAppWidgetManager(applicationContext)

            MainScope().launch {
                manager.setWidgetPreviews(ThirukkuralWidgetReceiver::class)
            }
        }
    }
}