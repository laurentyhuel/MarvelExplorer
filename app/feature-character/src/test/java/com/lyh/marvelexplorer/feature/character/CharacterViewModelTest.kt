package com.lyh.marvelexplorer.feature.character

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import com.lyh.marvelexplorer.domain.core.ResultError
import com.lyh.marvelexplorer.domain.core.ResultException
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.feature.character.detail.CharacterViewModel
import com.lyh.marvelexplorer.feature.character.model.CharacterUi
import com.lyh.marvelexplorer.feature.character.nav.CharacterDestination
import com.lyh.marvelexplorer.feature.character.util.CoroutinesTestExtension
import com.lyh.marvelexplorer.feature.character.util.InstantExecutorExtension
import com.lyh.marvelexplorer.feature.core.ResourceError
import com.lyh.marvelexplorer.feature.core.ResourceLoading
import com.lyh.marvelexplorer.feature.core.ResourceSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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

    private val characterId = 5
    private val squadCharacterUseCase = mockk<SquadCharacterUseCase> {
        every {isCharacterPresentInSquad(characterId)} returns flowOf(true)
    }
    private val savedStateHandle = mockk<SavedStateHandle> {
        every {get<Int>(CharacterDestination.characterIdArg)} returns characterId
    }

    @Test
    fun `WHEN call recruitSquadCharacter THEN useCase#addSquadCharacter must be called once`() =
        runTest {
            val characterUseCaseRelaxed = mockk<CharacterUseCase>(relaxed = true)
            val characterViewModel = CharacterViewModel(savedStateHandle, characterUseCaseRelaxed, squadCharacterUseCase)

            characterViewModel.recruitSquadCharacter(createCharacterUi(5))
            coVerify(exactly = 1) { squadCharacterUseCase.addSquadCharacter(any()) }
        }

    @Test
    fun `WHEN call fireSquadCharacter THEN useCase#deleteSquadCharacter must be called once`() =
        runTest {
            val characterUseCaseRelaxed = mockk<CharacterUseCase>(relaxed = true)
            val characterViewModel = CharacterViewModel(savedStateHandle, characterUseCaseRelaxed, squadCharacterUseCase)

            characterViewModel.fireSquadCharacter(createCharacterUi(5))
            coVerify(exactly = 1) { squadCharacterUseCase.deleteSquadCharacter(any()) }
        }

    @Test
    fun `WHEN get character by id succeed THEN get data`() = runTest {
        val characterUseCase = mockk<CharacterUseCase> {
            every {getCharacterById(characterId) } returns flowOf(ResultSuccess(createCharacterModel(characterId)))
        }
        val characterViewModel = CharacterViewModel(savedStateHandle, characterUseCase, squadCharacterUseCase)
        val character = createCharacterModel(characterId)

        characterViewModel.character.test {
            val result = awaitItem()
            assertTrue(result is ResourceSuccess)
            val characterResult = result as ResourceSuccess
            assertEquals(characterId, characterResult.data.id)
            assertEquals(character.name, characterResult.data.name)
        }
    }

    @Test
    fun `WHEN init THEN get character return loading`() = runTest {
        val characterUseCaseRelaxed = mockk<CharacterUseCase>(relaxed = true)
        val characterViewModel = CharacterViewModel(savedStateHandle, characterUseCaseRelaxed, squadCharacterUseCase)


        characterViewModel.character.test {
            val result = awaitItem()
            assertTrue(result is ResourceLoading)
        }
    }

    @Test
    fun `WHEN get character by id failed THEN get data`() = runTest {
        val characterUseCase = mockk<CharacterUseCase> {
            every {getCharacterById(characterId) } returns flowOf(ResultError(400, "Bad request"))
        }
        val characterViewModel = CharacterViewModel(savedStateHandle, characterUseCase, squadCharacterUseCase)

        characterViewModel.character.test {
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
