package com.lyh.marvelexplorer.data.mapper

import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity
import com.lyh.marvelexplorer.domain.model.SquadCharacterModel

internal fun SquadCharacterModel.toEntity() = SquadCharacterEntity(
    this.id,
    this.name,
    this.thumbnailUrl
)

internal fun List<SquadCharacterEntity>.toModels(): List<SquadCharacterModel> =
    this.map { it.toModel() }


internal fun SquadCharacterEntity.toModel() = SquadCharacterModel(
    this.id,
    this.name,
    this.thumbnailUrl
)