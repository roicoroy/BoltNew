package com.example.boltnew.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.provideDatabase
import com.example.boltnew.data.network.AdvertApiService
import com.example.boltnew.data.network.AddressApiService
import com.example.boltnew.data.network.AuthApiService
import com.example.boltnew.data.network.AuthenticatedHttpClient
import com.example.boltnew.data.network.TokenManager
import com.example.boltnew.data.network.UploadApiService
import com.example.boltnew.data.repository.AddressRepository
import com.example.boltnew.data.repository.AddressRepositoryImpl
import com.example.boltnew.data.repository.AdvertRepository
import com.example.boltnew.data.repository.AdvertRepositoryImpl
import com.example.boltnew.data.repository.AuthRepository
import com.example.boltnew.data.repository.AuthRepositoryImpl
import com.example.boltnew.data.repository.ProfileRepository
import com.example.boltnew.data.repository.ProfileRepositoryImpl
import com.example.boltnew.data.repository.UserAdvertRepository
import com.example.boltnew.data.repository.UserAdvertRepositoryImpl
import com.example.boltnew.presentation.viewmodel.AdvertViewModel
import com.example.boltnew.presentation.viewmodel.AdvertDetailViewModel
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import com.example.boltnew.presentation.viewmodel.AuthViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module {
    
    // Database
    single { provideDatabase(androidContext()) }
    single { get<com.example.boltnew.data.database.AppDatabase>().advertDao() }
    single { get<com.example.boltnew.data.database.AppDatabase>().profileDao() }
    
    // Token Management
    single { TokenManager(androidContext()) }
    single { AuthenticatedHttpClient(get()) }
    
    // Network
    single { AdvertApiService() }
    single { AuthApiService() }
    single { UploadApiService() }
    single { AddressApiService() }
    
    // Repository
    single<AdvertRepository> { AdvertRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AddressRepository> { AddressRepositoryImpl(get(), get(), get()) }
    single<UserAdvertRepository> { UserAdvertRepositoryImpl(get(), get(), get(), get()) }
    
    // ViewModels
    viewModel { AdvertViewModel(get(), get()) }
    viewModel { AdvertDetailViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
}