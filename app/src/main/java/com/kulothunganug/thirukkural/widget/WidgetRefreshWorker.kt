package com.kulothunganug.thirukkural.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.kulothunganug.thirukkural.datastore.FavouritesSettings
import com.kulothunganug.thirukkural.models.randomKuralId
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

const val MIN_AUTO_REFRESH_INTERVAL_MINUTES = 30
const val MAX_AUTO_REFRESH_INTERVAL_MINUTES = 24 * 60

private const val KEY_APPWIDGET_ID = "appWidgetId"

suspend fun pickKuralId(
    config: WidgetConfig,
    repository: ThirukkuralRepository,
    favouritesSettings: FavouritesSettings,
    currentId: Int? = null
): Int {
    val candidates: List<Int>? = when (config.refreshSource) {
        RefreshSource.All -> null
        RefreshSource.Category ->
            config.refreshCategoryPals.takeIf { it.isNotEmpty() }
                ?.let { pals ->
                    repository.getIdsFiltered(
                        pals, true,
                        config.refreshCategoryIyals, config.refreshCategoryIyals.isNotEmpty(),
                        config.refreshCategoryAdikarams, config.refreshCategoryAdikarams.isNotEmpty()
                    )
                }

        RefreshSource.Favourites -> favouritesSettings.favouriteIdsStream.first().toList()
    }

    return when {
        candidates.isNullOrEmpty() -> randomKuralId()
        candidates.size == 1 -> candidates.first()
        else -> candidates.filter { it != currentId }.random()
    }
}

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: ThirukkuralRepository by inject()
    private val favouritesSettings: FavouritesSettings by inject()

    override suspend fun doWork(): Result {
        val appWidgetId = inputData.getInt(KEY_APPWIDGET_ID, -1)
        if (appWidgetId == -1) return Result.failure()

        val glanceId = try {
            GlanceAppWidgetManager(applicationContext).getGlanceIdBy(appWidgetId)
        } catch (e: Exception) {
            return Result.failure()
        }

        updateAppWidgetState(applicationContext, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val config = prefs[WIDGET_CONFIG]?.let { Json.decodeFromString<WidgetConfig>(it) }
                ?: WidgetConfig()
            val currentId = prefs[ThirukkuralWidgetKeys.KURAL_ID]
            val newId = pickKuralId(config, repository, favouritesSettings, currentId)
            prefs.toMutablePreferences().apply {
                this[ThirukkuralWidgetKeys.KURAL_ID] = newId
            }
        }
        ThirukkuralWidget().update(applicationContext, glanceId)
        return Result.success()
    }
}

object WidgetRefreshScheduler {

    private fun workName(appWidgetId: Int) = "widget_auto_refresh_$appWidgetId"

    fun schedule(context: Context, appWidgetId: Int, intervalMinutes: Int) {
        val interval = intervalMinutes.coerceIn(
            MIN_AUTO_REFRESH_INTERVAL_MINUTES,
            MAX_AUTO_REFRESH_INTERVAL_MINUTES
        )
        val request = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(interval.toLong(), TimeUnit.MINUTES)
            .setInputData(workDataOf(KEY_APPWIDGET_ID to appWidgetId))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workName(appWidgetId),
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context, appWidgetId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(appWidgetId))
    }
}
