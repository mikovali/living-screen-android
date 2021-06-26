package com.sensorfields.livingscreen.android.ui.home

data class HomeState(
    val albums: List<AlbumItem> = emptyList(),
    val sharedAlbums: List<AlbumItem> = emptyList()
) {
    data class AlbumItem(val id: String, val title: String)
}
