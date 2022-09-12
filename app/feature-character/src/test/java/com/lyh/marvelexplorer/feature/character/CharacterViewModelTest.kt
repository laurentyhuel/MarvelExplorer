package com.lyh.marvelexplorer.feature.character

import app.cash.turbine.test
import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import com.lyh.marvelexplorer.domain.core.ResultException
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.feature.character.detail.CharacterViewModel
import com.lyh.marvelexplorer.feature.character.model.CharacterUi
import com.lyh.marvelexplorer.feature.character.util.CoroutinesTestExtension
import com.lyh.marvelexplorer.feature.character.util.InstantExecutorExtension
import com.lyh.marvelexplorer.feature.core.ResourceError
import com.lyh.marvelexplorer.feature.core.ResourceLoading
import com.lyh.marvelexplorer.feature.core.ResourceSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeoutException

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class CharacterViewModelTest {

    private val characterUseCase = mockk<CharacterUseCase>()
    private val squadCharacterUseCase = mockk<SquadCharacterUseCase>()
    private lateinit var characterViewModel: CharacterViewModel

    @BeforeEach
    fun initVM() {
        characterViewModel = CharacterViewModel(characterUseCase, squadCharacterUseCase)
    }

    @Test
    fun `WHEN call recruitSquadCharacter THEN useCase#addSquadCharacter must be called once`() =
        runTest {
            characterViewModel.recruitSquadCharacter(createCharacterUi(5))
            coVerify(exactly = 1) { squadCharacterUseCase.addSquadCharacter(any()) }
        }

    @Test
    fun `WHEN call fireSquadCharacter THEN useCase#deleteSquadCharacter must be called once`() =
        runTest {
            characterViewModel.fireSquadCharacter(createCharacterUi(5))
            coVerify(exactly = 1) { squadCharacterUseCase.deleteSquadCharacter(any()) }
        }

    @Test
    fun `WHEN get character by id succeed THEN get data`() = runTest {

        val characterId = 5
        val character = createCharacterModel(characterId)

        coEvery { characterUseCase.getCharacterById(characterId) } returns flowOf(
            ResultSuccess(
                character
            )
        )

        characterViewModel.character.test {
            val resultLoading = awaitItem()
            assertTrue(resultLoading is ResourceLoading)

            characterViewModel.setCharacterId(characterId)
            val result = awaitItem()
            assertTrue(result is ResourceSuccess)
            val characterResult = result as ResourceSuccess
            assertEquals(characterId, characterResult.data.id)
            assertEquals(character.name, characterResult.data.name)
        }
    }

    @Test
    fun `WHEN get character by id failed THEN get exception`() = runTest {


        coEvery { characterUseCase.getCharacterById(any()) } returns flowOf(
            ResultException(
                TimeoutException()
            )
        )

        characterViewModel.character.test {
            val resultLoading = awaitItem()
            assertTrue(resultLoading is ResourceLoading)

            characterViewModel.setCharacterId(5)
            val result = awaitItem()
            assertTrue(result is ResourceError)
        }
    }

    private fun createCharacterModel(id: Int) = CharacterModel(
        id,
        "Name $id",
        "Description for $id",
        "https://myurl.com/tiny/$id.jpg"
    )

    private fun createCharacterUi(id: Int) = CharacterUi(
        id,
        "Name $id",
        "Description for $id",
        "https://myurl.com/tiny/$id.jpg"
    )
}
