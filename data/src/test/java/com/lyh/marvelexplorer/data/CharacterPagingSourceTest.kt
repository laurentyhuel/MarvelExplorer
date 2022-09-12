package com.lyh.marvelexplorer.data

import androidx.paging.PagingSource
import com.lyh.marvelexplorer.data.core.MarvelException
import com.lyh.marvelexplorer.data.mapper.toModels
import com.lyh.marvelexplorer.data.remote.MarvelApi
import com.lyh.marvelexplorer.data.remote.dto.CharacterDto
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataContainer
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataWrapper
import com.lyh.marvelexplorer.data.remote.dto.MarvelImage
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.util.concurrent.TimeoutException

class CharacterPagingSourceTest {

    private val characters = List(20) { index -> createCharacterDto(index) }

    private val marvelApi = mockk<MarvelApi>()
    private val characterPagingSource = CharacterPagingSource(marvelApi)

    @Test
    fun `WHEN load characters return data THEN get data`() = runTest {
        coEvery { marvelApi.getCharacters(offset = 0) } returns Response.success(
            MarvelDataWrapper(
                code = 200,
                status = "Ok",
                etag = "",
                data = MarvelDataContainer(0, 20, 122, 20, characters)
            )
        )
        val result = characterPagingSource.load(
            PagingSource.LoadParams.Refresh(
            key = 0,
            loadSize = CharacterPagingSource.PAGE_SIZE,
            placeholdersEnabled = false
        ))

        assertEquals(
            PagingSource.LoadResult.Page(
                characters.toModels(),
                null,
                1
            ),
            result
        )
    }

    @Test
    fun `WHEN key = 1 THEN return prevKey equals null and nextKey equals 2`() = runTest {
        coEvery { marvelApi.getCharacters(offset = 20) } returns Response.success(
            MarvelDataWrapper(
                code = 200,
                status = "Ok",
                etag = "",
                data = MarvelDataContainer(20, 20, 122, 20, characters)
            )
        )
        val result = characterPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = CharacterPagingSource.PAGE_SIZE,
                placeholdersEnabled = false
            ))

        assertEquals(
            PagingSource.LoadResult.Page(
                characters.toModels(),
                null,
                2
            ),
            result
        )
    }

    @Test
    fun `WHEN load characters return marvel error THEN get error`() = runTest {
        coEvery { marvelApi.getCharacters(offset = 0) } returns Response.success(
            MarvelDataWrapper(
                code = 409,
                status = "Missing API Key",
                etag = "",
                data = null
            )
        )
        val result = characterPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = CharacterPagingSource.PAGE_SIZE,
                placeholdersEnabled = false
            ))


        assertTrue(result is PagingSource.LoadResult.Error)
        val resultError = result as PagingSource.LoadResult.Error
        assertTrue(resultError.throwable is MarvelException)
        val exception = resultError.throwable as MarvelException
        assertEquals(409, exception.code)
        assertEquals("Missing API Key", exception.message)
    }

    @Test
    fun `WHEN load characters return api error THEN get error`() = runTest {
        coEvery { marvelApi.getCharacters(offset = 0) } returns Response.error(418, "I'm a teapot".toResponseBody())

        val result = characterPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = CharacterPagingSource.PAGE_SIZE,
                placeholdersEnabled = false
            ))


        assertTrue(result is PagingSource.LoadResult.Error)
        val resultError = result as PagingSource.LoadResult.Error
        assertTrue(resultError.throwable is MarvelException)
        val exception = resultError.throwable as MarvelException
        assertEquals(418, exception.code)
    }

    @Test
    fun `WHEN load characters throws exception THEN get exception`() = runTest {
        coEvery { marvelApi.getCharacters(offset = 0) } throws TimeoutException()

        val result = characterPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = CharacterPagingSource.PAGE_SIZE,
                placeholdersEnabled = false
            ))


        assertTrue(result is PagingSource.LoadResult.Error)
        val resultError = result as PagingSource.LoadResult.Error
        assertTrue(resultError.throwable is TimeoutException)
    }

    private fun createCharacterDto(id: Int) = CharacterDto(
        1,
        "Name $id",
        "description for $id",
        MarvelImage("https://myurl.com/tiny/$id", "jpg")
    )
}