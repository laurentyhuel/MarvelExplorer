package com.lyh.marvelexplorer.feature.character.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import com.lyh.marvelexplorer.feature.character.mapper.toUis
import com.lyh.marvelexplorer.feature.character.model.SquadCharacterUi
import com.lyh.marvelexplorer.feature.core.Resource
import com.lyh.marvelexplorer.feature.core.ResourceLoading
import com.lyh.marvelexplorer.feature.core.ResourceSuccess
import kotlinx.coroutines.flow.*

class CharacterListViewModel(
    characterUseCase: CharacterUseCase,
    squadCharacterUseCase: SquadCharacterUseCase
) : ViewModel() {

    val characters = characterUseCase.getCharacters()

    val squadCharacters: StateFlow<Resource<List<SquadCharacterUi>>> =
        squadCharacterUseCase.getSquadCharacters()
            .map { ResourceSuccess(it.toUis()) }
            .onStart { ResourceLoading<List<SquadCharacterUi>>() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResourceLoading()
            )
}