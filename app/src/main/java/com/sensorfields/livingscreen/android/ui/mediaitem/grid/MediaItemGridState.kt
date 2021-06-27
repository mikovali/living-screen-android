package com.sensorfields.livingscreen.android.ui.mediaitem.grid

import com.sensorfields.livingscreen.android.model.MediaItem

object MediaItemGridState {

    data class MediaItemItem(val id: String, val baseUrl: String, val filename: String)
}

fun MediaItem.toItem(): MediaItemGridState.MediaItemItem {
    return MediaItemGridState.MediaItemItem(
        id = id,
        baseUrl = baseUrl,
        filename = filename
    )
}
