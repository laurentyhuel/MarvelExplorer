package com.lyh.marvelexplorer.domain

import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.domain.repository.ISquadCharacterRepository

class SquadCharacterUseCase(private val squadCharacterRepository: ISquadCharacterRepository) {

    fun isCharacterPresentInSquad(id: Int) = squadCharacterRepository.isCharacterPresentInSquad(id)

    fun getSquadCharacters() = squadCharacterRepository.getSquadCharacters()

    suspend fun addSquadCharacter(squadCharacterModel: SquadCharacterModel) = squadCharacterRepository.addSquadCharacter(squadCharacterModel)

    suspend fun deleteSquadCharacter(squadCharacterModel: SquadCharacterModel) = squadCharacterRepository.deleteSquadCharacter(squadCharacterModel)

}