package com.sensorfields.livingscreen.android.domain.data.dto

import com.sensorfields.livingscreen.android.model.MediaItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MediaItemDto(
    val id: String,
    val baseUrl: String,
    val filename: String,
    val mediaMetadata: Metadata
) {
    @Serializable
    data class Metadata(
        val photo: JsonObject? = null,
        val video: JsonObject? = null
    )
}

fun MediaItemDto.toModel(): MediaItem {
    return MediaItem(
        id = id,
        type = if (mediaMetadata.video != null) MediaItem.Type.Video else MediaItem.Type.Photo,
        baseUrl = baseUrl,
        fileName = filename
    )
}

fun List<MediaItemDto>.toModels(): List<MediaItem> = map { it.toModel() }
