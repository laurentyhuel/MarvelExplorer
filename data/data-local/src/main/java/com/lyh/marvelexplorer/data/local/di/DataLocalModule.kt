package com.lyh.marvelexplorer.data.local.di

import android.content.Context
import androidx.room.Room
import com.lyh.marvelexplorer.data.local.AppDatabase
import org.koin.dsl.module

/**
 * Koin module for data-local
 */
val dataLocalModule = module {
    single { providesAppDatabase(get()) }
    single { providesSquadCharacterDao(get()) }
}

fun providesSquadCharacterDao(appDatabase: AppDatabase) = appDatabase.squadCharacterDao()

fun providesAppDatabase(
    context: Context,
): AppDatabase = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    DATABASE_NAME
).build()

private const val DATABASE_NAME = "marvel-database"