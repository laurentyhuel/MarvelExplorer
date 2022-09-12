package com.lyh.marvelexplorer.feature.character.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import com.lyh.marvelexplorer.domain.core.ResultError
import com.lyh.marvelexplorer.domain.core.ResultException
import com.lyh.marvelexplorer.domain.core.ResultSuccess
import com.lyh.marvelexplorer.feature.character.R
import com.lyh.marvelexplorer.feature.character.mapper.toSquadModel
import com.lyh.marvelexplorer.feature.character.mapper.toUi
import com.lyh.marvelexplorer.feature.character.model.CharacterUi
import com.lyh.marvelexplorer.feature.character.model.SquadCharacterUi
import com.lyh.marvelexplorer.feature.core.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class CharacterViewModel(
//    savedStateHandle: SavedStateHandle,
    private val characterUseCase: CharacterUseCase,
    private val squadCharacterUseCase: SquadCharacterUseCase
) : ViewModel() {

    //TODO cannot use SavedStateHandle https://github.com/InsertKoinIO/koin/issues/1350
//    private val characterId: String = checkNotNull(
//        savedStateHandle[CharacterDestination.characterIdArg]
//    )

    fun recruitSquadCharacter(characterUi: CharacterUi) = viewModelScope.launch {
        squadCharacterUseCase.addSquadCharacter(characterUi.toSquadModel())
    }

    fun fireSquadCharacter(characterUi: CharacterUi) = viewModelScope.launch {
        squadCharacterUseCase.deleteSquadCharacter(characterUi.toSquadModel())
    }

    private val characterIdTrigger: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)

    fun setCharacterId(id: Int) = viewModelScope.launch {
        characterIdTrigger.emit(id)
    }

    val isCharacterPresentInSquad: StateFlow<Boolean?> =
        characterIdTrigger.flatMapLatest { id ->
            squadCharacterUseCase.isCharacterPresentInSquad(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val character: StateFlow<Resource<CharacterUi>> = characterIdTrigger.flatMapLatest {
        getCharacterFlow(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ResourceLoading()
    )

    private fun getCharacterFlow(id: Int?): Flow<Resource<CharacterUi>> =
        if (id == null) {
            flowOf(ResourceLoading())
        } else {
            characterUseCase.getCharacterById(id)
                .map {
                    when (it) {
                        is ResultSuccess -> {
                            val character = it.data.toUi()
                            ResourceSuccess(character)
                        }
                        is ResultError -> {
                            Timber.e("Failed to getCharacter")
                            ResourceError(
                                errorMessage = ErrorMessage.ErrorMessageString(
                                    it.message
                                )
                            )
                        }
                        is ResultException -> {
                            Timber.e(it.throwable, "Error when getCharacter")
                            ResourceError(
                                errorMessage = ErrorMessage.ErrorMessageResource(
                                    R.string.get_character_exception
                                )
                            )
                        }
                    }
                }.onStart {
                    emit(ResourceLoading())
                }
        }
}
