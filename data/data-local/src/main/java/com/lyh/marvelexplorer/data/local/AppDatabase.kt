package com.lyh.marvelexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lyh.marvelexplorer.data.local.dao.SquadCharacterDao
import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity

@Database(entities = [SquadCharacterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun squadCharacterDao(): SquadCharacterDao
}