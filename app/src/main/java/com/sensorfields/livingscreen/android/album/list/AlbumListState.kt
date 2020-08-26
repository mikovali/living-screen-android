package com.sensorfields.livingscreen.android.album.list

import com.sensorfields.livingscreen.android.domain.Album

data class AlbumListState(
    val albums: List<Album> = listOf(),
    val sharedAlbums: List<Album> = listOf()
)
