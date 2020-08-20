package com.sensorfields.livingscreen.android.domain.data.dto

import com.sensorfields.livingscreen.android.domain.MediaItem
import kotlinx.serialization.Serializable

@Serializable
data class MediaItemDto(val id: String, val filename: String)

fun MediaItemDto.toModel(): MediaItem {
    return MediaItem(
        id = id,
        fileName = filename
    )
}

fun List<MediaItemDto>.toModels(): List<MediaItem> = map { it.toModel() }
