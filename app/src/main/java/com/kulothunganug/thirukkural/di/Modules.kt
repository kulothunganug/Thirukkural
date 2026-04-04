package com.kulothunganug.thirukkural.di

import com.kulothunganug.thirukkural.ThirukkuralDatabase
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ThirukkuralDatabase.get(get()) }
    single { get<ThirukkuralDatabase>().dao() }
    single { ThirukkuralRepository(get()) }
    viewModel { HomeViewModel(get()) }
}