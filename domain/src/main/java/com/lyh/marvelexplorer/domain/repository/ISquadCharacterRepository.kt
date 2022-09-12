package com.lyh.marvelexplorer.domain.repository

import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import kotlinx.coroutines.flow.Flow

interface ISquadCharacterRepository {

    fun isCharacterPresentInSquad(id: Int): Flow<Boolean>

    fun getSquadCharacters() : Flow<List<SquadCharacterModel>>

    suspend fun addSquadCharacter(squadCharacterModel: SquadCharacterModel)

    suspend fun deleteSquadCharacter(squadCharacterModel: SquadCharacterModel)
}