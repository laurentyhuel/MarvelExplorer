package com.lyh.marvelexplorer.data

import app.cash.turbine.test
import com.lyh.marvelexplorer.data.core.AppDispatchers
import com.lyh.marvelexplorer.data.remote.MarvelApi
import com.lyh.marvelexplorer.data.remote.dto.CharacterDto
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataContainer
import com.lyh.marvelexplorer.data.remote.dto.MarvelDataWrapper
import com.lyh.marvelexplorer.data.remote.dto.MarvelImage
import com.lyh.marvelexplorer.domain.core.ResultError
import com.lyh.marvelexplorer.domain.core.ResultException
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.util.concurrent.TimeoutException

class CharacterRepositoryTest {

    private val marvelApi = mockk<MarvelApi>()
    private val characterPagingSource = mockk<CharacterPagingSource>()
    private val dispatchers = AppDispatchers(UnconfinedTestDispatcher(), UnconfinedTestDispatcher())
    private val characterRepository =
        CharacterRepository(marvelApi, characterPagingSource, dispatchers)

    @Test
    fun `WHEN getCharacterById succeed THEN return expected character`() = runTest {
        val character = createCharacterDto(5)

        coEvery { marvelApi.getCharacter(character.id) } returns Response.success(
            MarvelDataWrapper(
                code = 200,
                status = "Ok",
                etag = "",
                data = MarvelDataContainer(20, 20, 1, 1, listOf(character))
            )
        )

        characterRepository.getCharacterById(character.id)
            .test {
                val result = awaitItem()

                assertTrue(result is ResultSuccess)
                val resultSuccess = result as ResultSuccess

                assertEquals(character.id, resultSuccess.data.id)
                assertEquals(character.name, result.data.name)
                assertEquals(character.description, result.data.description)
                assertEquals(
                    "${character.thumbnail?.path}.${character.thumbnail?.extension}",
                    result.data.thumbnailUrl
                )

                awaitComplete()
            }
    }

    @Test
    fun `WHEN getCharacterById return marvel error THEN return ResultError`() = runTest {

        coEvery { marvelApi.getCharacter(5) } returns Response.success(
            MarvelDataWrapper(
                code = 409,
                status = "Missing API Key",
                etag = "",
                data = null
            )
        )

        characterRepository.getCharacterById(5).test {
            val result = awaitItem()

            assertTrue(result is ResultError)
            val resultError = result as ResultError
            assertEquals(409, resultError.code)
            assertEquals("Missing API Key", resultError.message)

            awaitComplete()
        }
    }

    @Test
    fun `WHEN getCharacterById return api error THEN return ResultError`() = runTest {

        coEvery { marvelApi.getCharacter(5) } returns Response.error(
            418,
            "I'm a teapot".toResponseBody()
        )

        characterRepository.getCharacterById(5).test {
            val result = awaitItem()

            assertTrue(result is ResultError)
            val resultError = result as ResultError
            assertEquals(418, resultError.code)

            awaitComplete()
        }
    }

    @Test
    fun `WHEN getCharacterById return exception THEN return ResultException`() = runTest {

        coEvery { marvelApi.getCharacter(5) } throws TimeoutException()

        characterRepository.getCharacterById(5).test {
            val result = awaitItem()

            assertTrue(result is ResultException)

            awaitComplete()
        }
    }

    private fun createCharacterDto(id: Int) = CharacterDto(
        1,
        "Name $id",
        "description for $id",
        MarvelImage("https://myurl.com/tiny/$id", "jpg")
    )
}
