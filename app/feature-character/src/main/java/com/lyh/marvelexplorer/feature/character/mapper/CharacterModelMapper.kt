package com.lyh.marvelexplorer.feature.character.mapper

import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.feature.character.model.CharacterUi

fun CharacterModel.toUi() = CharacterUi(
    this.id,
    this.name,
    this.description,
    this.thumbnailUrl
)

fun List<CharacterModel>.toUis() = this.map { it.toUi() }

fun CharacterUi.toSquadModel() = SquadCharacterModel(this.id, this.name, this.thumbnailUrl)