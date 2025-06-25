package com.example.boltnew.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        AdvertEntity::class, 
        ProfileEntity::class, 
        AddressEntity::class, 
        UserAdvertEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun advertDao(): AdvertDao
    abstract fun profileDao(): ProfileDao
    
    companion object {
        const val DATABASE_NAME = "advert_database"
    }
}

fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    )
    .fallbackToDestructiveMigration()
    .build()
}