package com.sensorfields.livingscreen.android.mapping

import com.sensorfields.livingscreen.android.data.dto.AlbumDto
import com.sensorfields.livingscreen.android.model.Album

fun AlbumDto.toModel(): Album {
    return Album(
        id = id,
        title = title
    )
}

fun List<AlbumDto>.toModels(): List<Album> = map { it.toModel() }
