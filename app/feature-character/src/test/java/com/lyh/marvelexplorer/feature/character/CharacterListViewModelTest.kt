package com.lyh.marvelexplorer.feature.character

import app.cash.turbine.test
import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.feature.character.list.CharacterListViewModel
import com.lyh.marvelexplorer.feature.character.util.CoroutinesTestExtension
import com.lyh.marvelexplorer.feature.character.util.InstantExecutorExtension
import com.lyh.marvelexplorer.feature.core.ResourceSuccess
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    InstantExecutorExtension::class,
    CoroutinesTestExtension::class
)
class CharacterListViewModelTest {

    private val characters = List(10) { index -> createSquadCharacterModel(index) }

    private val characterUseCase = mockk<CharacterUseCase>(relaxed = true)
    private val squadCharacterUseCase = mockk<SquadCharacterUseCase>()

    private lateinit var characterListViewModel: CharacterListViewModel


    @Test
    fun `WHEN get squadCharacters THEN return state with squad characters`() = runTest {
        coEvery { squadCharacterUseCase.getSquadCharacters() } returns flowOf(characters)
        characterListViewModel = CharacterListViewModel(characterUseCase, squadCharacterUseCase)


        characterListViewModel.squadCharacters.test {

            val dataSuccess = awaitItem()
            assertTrue(dataSuccess is ResourceSuccess)
            val resultSuccess = dataSuccess as ResourceSuccess
            assertEquals(characters.size, resultSuccess.data.size)
        }
    }

    private fun createSquadCharacterModel(id: Int) = SquadCharacterModel(
        id,
        "Name $id",
        "https://myurl.com/tiny/$id.jpg"
    )
}
