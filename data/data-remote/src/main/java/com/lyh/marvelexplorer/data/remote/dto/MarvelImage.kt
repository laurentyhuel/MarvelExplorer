package com.lyh.marvelexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarvelImage(
    val path: String?,
    val extension: String?
)