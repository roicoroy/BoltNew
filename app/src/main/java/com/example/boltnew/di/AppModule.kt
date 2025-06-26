package com.example.boltnew.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.provideDatabase
import com.example.boltnew.data.network.AdvertApiService
import com.example.boltnew.data.network.AddressApiService
import com.example.boltnew.data.network.AuthApiService
import com.example.boltnew.data.network.AuthenticatedHttpClient
import com.example.boltnew.data.network.ProfileApiService
import com.example.boltnew.data.network.TokenManager
import com.example.boltnew.data.network.UploadApiService
import com.example.boltnew.data.repository.address.AddressRepository
import com.example.boltnew.data.repository.address.AddressRepositoryImpl
import com.example.boltnew.data.repository.advert.AdvertRepository
import com.example.boltnew.data.repository.advert.AdvertRepositoryImpl
import com.example.boltnew.data.repository.auth.AuthRepository
import com.example.boltnew.data.repository.auth.AuthRepositoryImpl
import com.example.boltnew.data.repository.profile.ProfileRepository
import com.example.boltnew.data.repository.profile.ProfileRepositoryImpl
import com.example.boltnew.data.repository.user.UserAdvertRepository
import com.example.boltnew.data.repository.user.UserAdvertRepositoryImpl
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
    single { ProfileApiService() }

    // Repository
    single<AdvertRepository> { AdvertRepositoryImpl(get(), get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AddressRepository> { AddressRepositoryImpl(get(), get(), get()) }
    single<UserAdvertRepository> { UserAdvertRepositoryImpl(get(), get(), get(), get()) }

    // ViewModels
    viewModel { AdvertViewModel(get(), get()) }
    viewModel { AdvertDetailViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
}