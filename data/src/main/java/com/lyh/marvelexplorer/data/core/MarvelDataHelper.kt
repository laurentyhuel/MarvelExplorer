package com.lyh.marvelexplorer.data.core

import com.lyh.marvelexplorer.data.remote.dto.MarvelDataContainer
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataWrapper

/**
 * Check if paging state has next page
 * @param marvelDataContainer marvel paging state
 * @return true if next page available
 */
internal fun hasNextPage(marvelDataContainer: MarvelDataContainer<*>) : Boolean {
    val limit = marvelDataContainer.limit ?: 0
    val count = marvelDataContainer.count ?: 0
    val total = marvelDataContainer.total ?: 0
    val offset = marvelDataContainer.offset ?: 0

    return when {
        count < limit -> false
        offset + count >= total -> false
        else -> true
    }
}

internal fun isMarvelResponseSuccessful(marvelDataWrapper: MarvelDataWrapper<*>): Boolean =
    marvelDataWrapper.code in 200..299