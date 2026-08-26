package com.kulothunganug.thirukkural.di

import androidx.room.Room
import com.kulothunganug.thirukkural.ThirukkuralDatabase
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import com.kulothunganug.thirukkural.viewmodels.BrowseViewModel
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel
import com.kulothunganug.thirukkural.viewmodels.KuralDetailViewModel
import com.kulothunganug.thirukkural.viewmodels.SettingsViewModel
import com.kulothunganug.thirukkural.viewmodels.WidgetCustomizationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ThirukkuralDatabase::class.java,
            "thirukkural.db"
        )
            // thirukkural.db is a static, read-only reference dataset bundled as an asset and
            // never written to at runtime, so a destructive migration is safe: it simply
            // re-syncs an upgrading install to the latest bundled content instead of requiring
            // a hand-written Migration for data that never changes shape from user input.
            .fallbackToDestructiveMigration(true)
            .createFromAsset("thirukkural.db")
            .build()
    }
    single { get<ThirukkuralDatabase>().dao() }
    single { ThirukkuralRepository(get()) }
    single { ThemeSettings(androidContext()) }
    viewModel { HomeViewModel(get()) }
    viewModel { BrowseViewModel(get()) }
    viewModel { KuralDetailViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (appWidgetId: Int) -> WidgetCustomizationViewModel(androidContext(), appWidgetId) }
}