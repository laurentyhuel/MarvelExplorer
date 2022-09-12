package com.lyh.marvelexplorer.data.remote

import com.lyh.marvelexplorer.data.remote.dto.CharacterDto
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataContainer
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelApi {

    @GET("characters")
    suspend fun getCharacters(@Query("limit") limit: Int? = null, @Query("offset") offset: Int?): Response<MarvelDataWrapper<MarvelDataContainer<CharacterDto>?>>

    @GET("characters/{id}")
    suspend fun getCharacter(@Path("id") id: Int?): Response<MarvelDataWrapper<MarvelDataContainer<CharacterDto>?>>
}

