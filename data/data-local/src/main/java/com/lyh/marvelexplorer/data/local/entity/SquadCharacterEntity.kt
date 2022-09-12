package com.lyh.marvelexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lyh.marvelexplorer.data.local.entity.SquadCharacterEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class SquadCharacterEntity(
    @PrimaryKey
    var id: Int,
    var name: String,
    var thumbnailUrl: String?,
) {
    companion object {
        const val TABLE_NAME = "squad_character"
    }
}

