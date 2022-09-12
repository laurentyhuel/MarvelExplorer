package com.lyh.marvelexplorer.data

import com.lyh.marvelexplorer.data.local.dao.SquadCharacterDao
import com.lyh.marvelexplorer.data.mapper.toEntity
import com.lyh.marvelexplorer.data.mapper.toModels
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.domain.repository.ISquadCharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SquadCharacterRepository(
    private val squadCharacterDao: SquadCharacterDao
): ISquadCharacterRepository {

    override fun isCharacterPresentInSquad(id: Int): Flow<Boolean> = squadCharacterDao.getCharacterByIdFromSquad(id).map { it.firstOrNull() != null }

    override fun getSquadCharacters(): Flow<List<SquadCharacterModel>> = squadCharacterDao.getSquadCharacters().map { it.toModels() }

    override suspend fun addSquadCharacter(squadCharacterModel: SquadCharacterModel) {
        squadCharacterDao.insertOrUpdateSquadCharacter(squadCharacterModel.toEntity())
    }

    override suspend fun deleteSquadCharacter(squadCharacterModel: SquadCharacterModel) {
        squadCharacterDao.deleteSquadCharacter(squadCharacterModel.toEntity())
    }
}