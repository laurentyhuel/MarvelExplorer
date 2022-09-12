package com.lyh.marvelexplorer.feature.character.mapper

import com.lyh.marvelexplorer.domain.model.SquadCharacterModel
import com.lyh.marvelexplorer.feature.character.model.SquadCharacterUi


fun SquadCharacterModel.toUi() = SquadCharacterUi(
    this.id,
    this.name,
    this.thumbnailUrl
)

fun List<SquadCharacterModel>.toUis() = this.map { it.toUi() }