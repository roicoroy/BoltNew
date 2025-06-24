package com.example.boltnew.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [AdvertEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun advertDao(): AdvertDao
    abstract fun userDao(): UserDao
    
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