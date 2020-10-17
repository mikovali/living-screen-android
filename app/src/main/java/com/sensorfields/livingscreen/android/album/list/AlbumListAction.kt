package com.sensorfields.livingscreen.android.album.list

sealed class AlbumListAction {
    object NavigateToAccountCreate : AlbumListAction()
    data class NavigateToMediaItemDetails(val index: Int) : AlbumListAction()
    data class NavigateToMediaItemView(val index: Int) : AlbumListAction()
}
