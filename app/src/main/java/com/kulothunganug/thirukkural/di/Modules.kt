package com.kulothunganug.thirukkural.di

import com.kulothunganug.thirukkural.ThirukkuralDatabase
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel
import com.kulothunganug.thirukkural.viewmodels.KuralDetailViewModel
import com.kulothunganug.thirukkural.viewmodels.WidgetConfigurationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ThirukkuralDatabase.get(get()) }
    single { get<ThirukkuralDatabase>().dao() }
    single { ThirukkuralRepository(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { KuralDetailViewModel(get()) }
    viewModel { (appWidgetId: Int) -> WidgetConfigurationViewModel(androidContext(), appWidgetId) }
}