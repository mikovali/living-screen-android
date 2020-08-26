package com.sensorfields.livingscreen.android.domain.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sensorfields.livingscreen.android.domain.Album
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class AlbumDto(
    @PrimaryKey val id: String,
    val title: String = "",
    val isShared: Boolean = false
)

fun AlbumDto.toModel(): Album {
    return Album(
        id = id,
        title = title
    )
}

fun List<AlbumDto>.toModels(): List<Album> = map { it.toModel() }
