package com.lyh.marvelexplorer.domain

import com.lyh.marvelexplorer.domain.core.Result
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.domain.repository.ICharacterRepository
import kotlinx.coroutines.flow.Flow

class CharacterUseCase(private val characterRepository: ICharacterRepository) {

    fun getCharacters() = characterRepository.getCharacters()

    fun getCharacterById(id: Int): Flow<Result<CharacterModel>> = characterRepository.getCharacterById(id)

}
