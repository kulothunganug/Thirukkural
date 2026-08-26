package com.kulothunganug.thirukkural

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kulothunganug.thirukkural.di.appModule
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

private const val TAG = "ThirukkuralApplication"

class ThirukkuralApplication : Application() {

    // A real application-scoped coroutine scope instead of an ad-hoc MainScope() per call site,
    // so any future application-level background work has one consistent, cancellable home.
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        startKoin {
            if (isDebuggable) {
                androidLogger(Level.INFO)
            }
            androidContext(this@ThirukkuralApplication)
            modules(appModule)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val manager = GlanceAppWidgetManager(applicationContext)

            applicationScope.launch {
                // This is a purely cosmetic nicety (richer widget-picker previews) — it must
                // never be allowed to crash app startup on a device/OS combination where the
                // Glance preview API misbehaves.
                try {
                    manager.setWidgetPreviews(ThirukkuralWidgetReceiver::class)
                } catch (e: Exception) {
                    Log.w(TAG, "failed to set widget previews", e)
                }
            }
        }
    }
}
