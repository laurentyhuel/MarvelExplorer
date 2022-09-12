package com.lyh.marvelexplorer.domain.model

data class CharacterModel(
    val id: Int,
    val name: String,
    val description: String?,
    val thumbnailUrl: String?
)