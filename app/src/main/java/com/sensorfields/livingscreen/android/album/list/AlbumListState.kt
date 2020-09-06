package com.sensorfields.livingscreen.android.album.list

import com.sensorfields.livingscreen.android.domain.Album
import com.sensorfields.livingscreen.android.domain.MediaItem

data class AlbumListState(
    val albums: List<Album> = listOf(),
    val sharedAlbums: List<Album> = listOf()
)

data class MediaItemGridState(
    val items: List<Item> = listOf()
) {
    data class Item(
        val index: Int,
        val type: MediaItem.Type,
        val baseUrl: String,
        val fileName: String
    )
}

data class MediaItemViewState(
    val current: MediaItemGridState.Item,
    val previous: MediaItemGridState.Item?,
    val next: MediaItemGridState.Item?
)
