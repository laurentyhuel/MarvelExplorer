package com.lyh.marvelexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarvelDataContainer<T>(
    val offset: Int?,
    val limit: Int?,
    val total: Int?,
    val count: Int?,
    val results: List<T>?
)