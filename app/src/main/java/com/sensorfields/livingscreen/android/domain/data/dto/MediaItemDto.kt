package com.sensorfields.livingscreen.android.domain.data.dto

import androidx.core.net.toUri
import com.sensorfields.livingscreen.android.domain.MediaItem
import kotlinx.serialization.Serializable

@Serializable
data class MediaItemDto(
    val id: String,
    val baseUrl: String,
    val filename: String
)

fun MediaItemDto.toModel(): MediaItem {
    return MediaItem(
        id = id,
        thumbnail = "$baseUrl=w1024-h768-c".toUri(), // TODO move
        fileName = filename
    )
}

fun List<MediaItemDto>.toModels(): List<MediaItem> = map { it.toModel() }
