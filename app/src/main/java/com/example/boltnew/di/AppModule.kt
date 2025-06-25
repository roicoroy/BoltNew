package com.example.boltnew.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.provideDatabase
import com.example.boltnew.data.network.AdvertApiService
import com.example.boltnew.data.repository.AdvertRepository
import com.example.boltnew.data.repository.AdvertRepositoryImpl
import com.example.boltnew.data.repository.ProfileRepository
import com.example.boltnew.data.repository.ProfileRepositoryImpl
import com.example.boltnew.presentation.viewmodel.HomeViewModel
import com.example.boltnew.presentation.viewmodel.AdvertDetailViewModel
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module {
    
    // Database
    single { provideDatabase(androidContext()) }
    single { get<com.example.boltnew.data.database.AppDatabase>().advertDao() }
    single { get<com.example.boltnew.data.database.AppDatabase>().profileDao() }
    
    // Network
    single { AdvertApiService() }
    
    // Repository
    single<AdvertRepository> { AdvertRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    
    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { AdvertDetailViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}