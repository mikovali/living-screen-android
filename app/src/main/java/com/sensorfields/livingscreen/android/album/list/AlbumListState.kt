package com.sensorfields.livingscreen.android.album.list

import androidx.paging.PagingData
import com.sensorfields.livingscreen.android.model.Album
import com.sensorfields.livingscreen.android.model.MediaItem

data class AlbumListState(
    val albums: List<Album> = listOf(),
    val sharedAlbums: List<Album> = listOf()
)

data class MediaItemGridState(
    val items: PagingData<Item> = PagingData.empty()
) {
    data class Item(
        val id: String,
        val index: Int,
        val type: MediaItem.Type,
        val baseUrl: String,
        val fileName: String
    )
}

data class MediaItemViewState(
    val type: MediaItem.Type,
    val baseUrl: String,
    val fileName: String,
    val isPreviousVisible: Boolean,
    val isNextVisible: Boolean
)
