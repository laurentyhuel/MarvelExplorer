package com.lyh.marvelexplorer.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lyh.marvelexplorer.data.core.MarvelException
import com.lyh.marvelexplorer.data.core.hasNextPage
import com.lyh.marvelexplorer.data.core.isMarvelResponseSuccessful
import com.lyh.marvelexplorer.data.mapper.toModels
import com.lyh.marvelexplorer.data.remote.MarvelApi
import com.lyh.marvelexplorer.data.remote.core.ApiError
import com.lyh.marvelexplorer.data.remote.core.ApiException
import com.lyh.marvelexplorer.data.remote.core.ApiSuccess
import com.lyh.marvelexplorer.data.remote.core.callApi
import com.lyh.marvelexplorer.domain.model.CharacterModel
import java.lang.Integer.max

class CharacterPagingSource(
    private val marvelApi: MarvelApi
) : PagingSource<Int, CharacterModel>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CharacterModel> {
        try {
            val page = params.key ?: STARTING_KEY

            return when (val apiResult =
                callApi { marvelApi.getCharacters(offset = page * PAGE_SIZE) }) {
                is ApiSuccess -> {
                    val container = apiResult.data.data
                    val charactersDto = container?.results
                    if (isMarvelResponseSuccessful(apiResult.data) && charactersDto != null) {
                        return LoadResult.Page(
                            data = charactersDto.toModels(),
                            prevKey = null, // no backward paging
                            nextKey = if (hasNextPage(container)) page + 1 else null
                        )
                    } else {
                        LoadResult.Error(
                            MarvelException(
                                apiResult.data.code ?: 400,
                                apiResult.data.status ?: "Error when calling API"
                            )
                        )
                    }
                }
                is ApiError -> LoadResult.Error(MarvelException(apiResult.code, apiResult.message))
                is ApiException -> LoadResult.Error(apiResult.throwable)
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

//    override fun getRefreshKey(state: PagingState<Int, CharacterDto>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer
        val anchorPosition = state.anchorPosition ?: return null
        val character = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = character.id - (state.config.pageSize / 2))
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    companion object {
        const val STARTING_KEY = 0
        const val PAGE_SIZE = 20
    }
}