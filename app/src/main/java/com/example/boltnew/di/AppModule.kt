package com.example.boltnew.di

import com.example.boltnew.data.database.provideDatabase
import com.example.boltnew.data.repository.ProductRepository
import com.example.boltnew.data.repository.ProductRepositoryImpl
import com.example.boltnew.data.repository.UserRepository
import com.example.boltnew.data.repository.UserRepositoryImpl
import com.example.boltnew.presentation.viewmodel.HomeViewModel
import com.example.boltnew.presentation.viewmodel.ProductDetailViewModel
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // Database
    single { provideDatabase(androidContext()) }
    single { get<com.example.boltnew.data.database.AppDatabase>().productDao() }
    single { get<com.example.boltnew.data.database.AppDatabase>().userDao() }
    
    // Repository
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    
    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}