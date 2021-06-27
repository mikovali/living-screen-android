package com.sensorfields.livingscreen.android.mapping

import com.sensorfields.livingscreen.android.data.dto.MediaItemDto
import com.sensorfields.livingscreen.android.model.MediaItem

fun MediaItemDto.toModel(): MediaItem {
    return MediaItem(
        id = id,
        type = if (mediaMetadata.video != null) MediaItem.Type.Video else MediaItem.Type.Photo,
        baseUrl = baseUrl,
        filename = filename
    )
}

fun List<MediaItemDto>.toModels(): List<MediaItem> = map { it.toModel() }
