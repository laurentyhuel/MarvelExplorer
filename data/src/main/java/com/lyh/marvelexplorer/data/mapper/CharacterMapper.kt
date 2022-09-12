package com.lyh.marvelexplorer.data.mapper

import com.lyh.marvelexplorer.data.remote.dto.CharacterDto
import com.lyh.marvelexplorer.data.remote.dto.MarvelImage
import com.lyh.marvelexplorer.domain.model.CharacterModel

internal fun List<CharacterDto>.toModels() = this.map { it.toModel() }

internal fun CharacterDto.toModel() = CharacterModel(
    this.id,
    this.name,
    this.description,
    marvelImageToUrl(this.thumbnail)
)

private fun marvelImageToUrl(image: MarvelImage?): String? =
    if (image?.path != null && image.extension != null) "${image.path}.${image.extension}" else null


