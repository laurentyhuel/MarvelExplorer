package com.lyh.marvelexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarvelDataWrapper<T>(
    val code: Int?,
    val status: String?,
    val etag: String?,
    val data: T?
)
