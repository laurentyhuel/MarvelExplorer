package com.lyh.marvelexplorer.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.lyh.marvelexplorer.data.local.dao.SquadCharacterDao
import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SquadCharacterDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var squadCharacterDao: SquadCharacterDao
    private val squadCharacter1 = createSquadCharacter(5)
    private val squadCharacter2 = createSquadCharacter(6)

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        squadCharacterDao = database.squadCharacterDao()
    }

    @AfterEach
    fun closeDb() = database.close()

    @Test
    fun testInsertData() = runBlocking {

        squadCharacterDao.insertOrUpdateSquadCharacter(squadCharacter1)

        squadCharacterDao.getSquadCharacters().test {
            val squadCharacterInserted = awaitItem()
            assertEquals(1, squadCharacterInserted.size)
            // Character is a data class, so check is made on properties, and not instance :)
            assertEquals(squadCharacterInserted.first(), squadCharacter1)
        }

    }

    @Test
    fun testDeleteData() = runBlocking {

        squadCharacterDao.insertOrUpdateSquadCharacter(squadCharacter1)
        squadCharacterDao.insertOrUpdateSquadCharacter(squadCharacter2)

        squadCharacterDao.getSquadCharacters().test {
            val list = awaitItem()
            assertEquals(2 , list.size)

            // delete
            squadCharacterDao.deleteSquadCharacter(squadCharacter1)

            val listAfterDelete = awaitItem()
            assertEquals(1, listAfterDelete.size)
            assertEquals(squadCharacter2, listAfterDelete.first())
        }
    }

    @Test
    fun testGetCharacterByIdFromSquad() = runBlocking {
        val id = 5
        squadCharacterDao.getCharacterByIdFromSquad(id).test {
            val first = awaitItem().firstOrNull()
            assertNull(first)

            squadCharacterDao.insertOrUpdateSquadCharacter(squadCharacter1)

            val second = awaitItem().firstOrNull()
            assertNotNull(second)
        }
    }

    private fun createSquadCharacter(id: Int) = SquadCharacterEntity(
        id,
        "Name $id",
        "https://myurl.com/$id.jpg",
    )
}

