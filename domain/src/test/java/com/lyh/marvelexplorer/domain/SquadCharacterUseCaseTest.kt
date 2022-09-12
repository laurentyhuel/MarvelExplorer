package com.lyh.marvelexplorer.domain

import app.cash.turbine.test
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.domain.repository.ISquadCharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SquadCharacterUseCaseTest {

    private val squadCharacterRepository = mockk<ISquadCharacterRepository>()
    private val models = List(10) { index -> createSquadCharacterModel(index) }

    private val squadCharacterUseCase = SquadCharacterUseCase(this.squadCharacterRepository)

    @Test
    fun `WHEN isCharacterPresentInSquad present THEN return true`() = runTest {
        val id = 5
        coEvery { squadCharacterRepository.isCharacterPresentInSquad(id) } returns flow { emit(true)}

        squadCharacterUseCase.isCharacterPresentInSquad(id).test {
            val result = awaitItem()
            assertTrue(result)
            awaitComplete()
        }
    }

    @Test
    fun `WHEN isCharacterPresentInSquad absent THEN return false`() = runTest {
        val id = 5
        coEvery { squadCharacterRepository.isCharacterPresentInSquad(id) } returns flow { emit(false)}

        squadCharacterUseCase.isCharacterPresentInSquad(id).test {
            val result = awaitItem()
            assertFalse(result)
            awaitComplete()
        }
    }

    @Test
    fun `WHEN getSquadCharacters THEN return squad characters`() = runTest {
        coEvery { squadCharacterRepository.getSquadCharacters() } returns flow {emit(models)}


        squadCharacterUseCase.getSquadCharacters().test {
            val list = awaitItem()

            assertEquals(10, list.size)
            assertEquals(models.first(), list.first())
            awaitComplete()
        }
    }

    @Test
    fun `WHEN addSquadCharacter THEN repo#addSquadCharacter must be called`() = runTest {
        coEvery { squadCharacterRepository.addSquadCharacter(any()) } returns Unit

        squadCharacterUseCase.addSquadCharacter(createSquadCharacterModel(1))

        coVerify(exactly=1) { squadCharacterRepository.addSquadCharacter(any()) }
    }

    @Test
    fun `WHEN deleteSquadCharacter THEN repo#deleteSquadCharacter must be called`() = runTest {
        coEvery { squadCharacterRepository.deleteSquadCharacter(any()) } returns Unit

        squadCharacterUseCase.deleteSquadCharacter(createSquadCharacterModel(1))

        coVerify(exactly=1) { squadCharacterRepository.deleteSquadCharacter(any()) }
    }

    private fun createSquadCharacterModel(id: Int) = SquadCharacterModel(
        1,
        "Name $id",
        "https://myurl.com/tiny/$id.jpg"
    )
}