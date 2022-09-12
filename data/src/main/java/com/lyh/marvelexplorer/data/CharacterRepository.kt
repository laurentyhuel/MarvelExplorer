package com.lyh.marvelexplorer.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lyh.marvelexplorer.data.core.AppDispatchers
import com.lyh.marvelexplorer.data.core.isMarvelResponseSuccessful
import com.lyh.marvelexplorer.data.mapper.toModel
import com.lyh.marvelexplorer.data.remote.MarvelApi
import com.lyh.marvelexplorer.data.remote.core.ApiError
import com.lyh.marvelexplorer.data.remote.core.ApiException
import com.lyh.marvelexplorer.data.remote.core.ApiSuccess
import com.lyh.marvelexplorer.data.remote.core.callApi
import com.lyh.marvelexplorer.domain.core.Result
import com.lyh.marvelexplorer.domain.core.ResultError
import com.lyh.marvelexplorer.domain.core.ResultException
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.domain.repository.ICharacterRepository
import kotlinx.coroutines.flow.*

class CharacterRepository(
    private val marvelApi: MarvelApi,
    private val characterPagingSource: CharacterPagingSource,
    private val dispatchers: AppDispatchers
) : ICharacterRepository {

    override fun getCharacterById(id: Int): Flow<Result<CharacterModel>> = flow {
        when (val apiResult = callApi { marvelApi.getCharacter(id) }) {
            is ApiSuccess -> {
                val characterDto = apiResult.data.data?.results?.first()
                if (isMarvelResponseSuccessful(apiResult.data) && characterDto != null) {
                    emit(ResultSuccess(characterDto.toModel()))
                } else {
                    emit(
                        ResultError(
                            apiResult.data.code ?: 400,
                            apiResult.data.status ?: "Error when calling API"
                        )
                    )
                }
            }
            is ApiError -> emit(ResultError(apiResult.code, apiResult.message))
            is ApiException -> emit(ResultException(apiResult.throwable))
        }
    }.catch {
        emit(ResultException(it))
    }.flowOn(dispatchers.io)

    override fun getCharacters(): Flow<PagingData<CharacterModel>> = Pager(
        PagingConfig(CharacterPagingSource.PAGE_SIZE),
        pagingSourceFactory = {characterPagingSource}
    ).flow




}
