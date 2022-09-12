package com.lyh.marvelexplorer.data.core

import com.lyh.marvelexplorer.data.remote.dto.MarvelDataContainer
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataWrapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MarvelDataHelperTest {

    @Test
    fun `WHEN count lower than limit THEN hasNextPage return false`() {
        val result = hasNextPage(
            MarvelDataContainer<Unit>(
                offset = 0,
                limit = 20,
                total = 12,
                count = 12,
                results = null,
            ) )
        assertFalse (result)
    }

    @Test
    fun `WHEN offset plus count greater or equals than total THEN hasNextPage return false`() {
        val result = hasNextPage(
            MarvelDataContainer<Unit>(
                offset = 40,
                limit = 20,
                total = 60,
                count = 20,
                results = null,
            ) )
        assertFalse (result)
    }

    @Test
    fun `WHEN remaining elements total THEN hasNextPage return true`() {
        val result = hasNextPage(
            MarvelDataContainer<Unit>(
                offset = 40,
                limit = 20,
                total = 61,
                count = 20,
                results = null,
            ) )
        assertTrue (result)
    }

    @Test
    fun `WHEN marvel response code is successful THEN isMarvelResponseSuccessful return true`() {
        val result = isMarvelResponseSuccessful(
            MarvelDataWrapper<Unit>(
                code = 200,
                status = null,
                etag = null,
                data = null
            )
        )

        assertTrue(result)
    }

    @Test
    fun `WHEN marvel response code is failed THEN isMarvelResponseSuccessful return false`() {
        val result = isMarvelResponseSuccessful(
            MarvelDataWrapper<Unit>(
                code = 400,
                status = null,
                etag = null,
                data = null
            )
        )

        assertFalse(result)
    }
}