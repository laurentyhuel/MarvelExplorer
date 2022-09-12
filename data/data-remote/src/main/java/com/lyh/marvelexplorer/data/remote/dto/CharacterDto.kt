package com.lyh.marvelexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val description: String?,
    val thumbnail: MarvelImage?
)