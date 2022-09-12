package com.lyh.marvelexplorer.data.local.dao

import androidx.room.*
import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SquadCharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSquadCharacter(squadCharacter: SquadCharacterEntity)

    @Delete
    suspend fun deleteSquadCharacter(squadCharacter: SquadCharacterEntity)

    @Query(value = "SELECT * FROM ${SquadCharacterEntity.TABLE_NAME}")
    fun getSquadCharacters(): Flow<List<SquadCharacterEntity>>

    @Query(value = "SELECT * FROM ${SquadCharacterEntity.TABLE_NAME} WHERE id = :id")
    fun getCharacterByIdFromSquad(id: Int): Flow<List<SquadCharacterEntity>>
}