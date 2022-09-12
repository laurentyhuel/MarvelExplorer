package com.lyh.marvelexplorer.domain.repository

import androidx.paging.PagingData
import com.lyh.marvelexplorer.domain.core.Result
import com.lyh.marvelexplorer.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface ICharacterRepository {
    fun getCharacters(): Flow<PagingData<CharacterModel>>

    fun getCharacterById(id: Int): Flow<Result<CharacterModel>>
}
