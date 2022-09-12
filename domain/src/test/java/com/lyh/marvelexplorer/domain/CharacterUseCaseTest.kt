package com.lyh.marvelexplorer.domain

import androidx.paging.PagingData
import app.cash.turbine.test
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.domain.repository.ICharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CharacterUseCaseTest {

    private val characters = List(20) { index -> createCharacterModel(index.toLong()) }

    private val characterRepository = mockk<ICharacterRepository>(relaxed = true)
    private val characterUseCase = CharacterUseCase(characterRepository)

    @Test
    fun `WHEN getCharacters succeed THEN return characters`() = runTest {

        coEvery { characterUseCase.getCharacters() } returns flowOf(PagingData.from(characters))
        characterUseCase.getCharacters()
            .test {
                val result = awaitItem()
                assertNotNull(result)
                awaitComplete()
            }
    }

    @Test
    fun `WHEN getCharacterById succeed THEN return character`() = runTest {

        val character = createCharacterModel(5L)

        coEvery { characterUseCase.getCharacterById(character.id) } returns flowOf(ResultSuccess(character))
        characterUseCase.getCharacterById(character.id)
            .test {
                val result = awaitItem()

                assertTrue(result is ResultSuccess)
                val resultSuccess = result as ResultSuccess

                assertEquals(character.id, resultSuccess.data.id)
                assertEquals(character.name, resultSuccess.data.name)

                awaitComplete()
            }
    }

    private fun createCharacterModel(id: Long) = CharacterModel(
        1,
        "Name $id",
        "description $id",
        "https://myurl.com/tiny/$id.jpg"
    )
}
