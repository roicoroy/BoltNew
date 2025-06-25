package com.example.boltnew

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ProductApplication : Application() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ProductApplication)
            modules(appModule)
        }
    }
}