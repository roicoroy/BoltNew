package com.example.boltnew.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    
    companion object {
        const val DATABASE_NAME = "product_database"
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