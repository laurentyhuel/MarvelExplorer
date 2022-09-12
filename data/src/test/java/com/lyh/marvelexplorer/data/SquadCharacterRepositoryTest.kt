package com.lyh.marvelexplorer.data

import app.cash.turbine.test
import com.lyh.marvelexplorer.data.local.dao.SquadCharacterDao
import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity
import com.lyh.marvelexplorer.data.mapper.toModel
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SquadCharacterRepositoryTest {

    private val squadCharacterDao = mockk<SquadCharacterDao>()
    private val entities = List(10) { index -> createSquadCharacterEntity(index) }

    private val squadCharacterRepository = SquadCharacterRepository(squadCharacterDao)

    @Test
    fun `WHEN getSquadCharacters THEN return squad characters`() = runTest {
        coEvery { squadCharacterDao.getSquadCharacters() } returns flowOf(entities)


        squadCharacterRepository.getSquadCharacters().test {
            val list = awaitItem()

            assertEquals(10, list.size)
            assertEquals(entities.first().toModel(), list.first())
            awaitComplete()
        }
    }

    @Test
    fun `WHEN addSquadCharacter THEN dao#insertOrUpdateSquadCharacter must be called`() = runTest {
        coEvery { squadCharacterDao.insertOrUpdateSquadCharacter(any()) } returns Unit

        squadCharacterRepository.addSquadCharacter(createSquadCharacterModel(1))

        coVerify(exactly = 1) { squadCharacterDao.insertOrUpdateSquadCharacter(any()) }
    }

    @Test
    fun `WHEN deleteSquadCharacter THEN dao#deleteSquadCharacter must be called`() = runTest {
        coEvery { squadCharacterDao.deleteSquadCharacter(any()) } returns Unit

        squadCharacterRepository.deleteSquadCharacter(createSquadCharacterModel(1))

        coVerify(exactly = 1) { squadCharacterDao.deleteSquadCharacter(any()) }
    }

    @Test
    fun `WHEN isCharacterPresentInSquad and present THEN return true`() = runTest {
        val id = 5

        coEvery { squadCharacterDao.getCharacterByIdFromSquad(id)} returns flowOf(listOf(createSquadCharacterEntity(id)))

        squadCharacterRepository.isCharacterPresentInSquad(id).test {
            val result = awaitItem()
            assertTrue(result)
            awaitComplete()
        }
    }

    @Test
    fun `WHEN isCharacterPresentInSquad and absent THEN return false`() = runTest {

        coEvery { squadCharacterDao.getCharacterByIdFromSquad(any())} returns flowOf(emptyList())
        squadCharacterRepository.isCharacterPresentInSquad(5).test {
            val result = awaitItem()
            assertFalse(result)
            awaitComplete()
        }
    }

    private fun createSquadCharacterEntity(id: Int) = SquadCharacterEntity(
        1,
        "Name $id",
        "https://myurl.com/tiny/$id.jpg"
    )

    private fun createSquadCharacterModel(id: Int) = SquadCharacterModel(
        1,
        "Name $id",
        "https://myurl.com/tiny/$id.jpg"
    )
}