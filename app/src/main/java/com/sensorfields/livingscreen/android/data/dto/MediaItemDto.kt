package com.sensorfields.livingscreen.android.data.dto

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
