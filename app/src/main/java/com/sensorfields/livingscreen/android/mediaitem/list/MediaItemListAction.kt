package com.sensorfields.livingscreen.android.mediaitem.list

import com.sensorfields.livingscreen.android.domain.MediaItem

sealed class MediaItemListAction {
    object NavigateToAccountCreate : MediaItemListAction()
    data class NavigateToMediaItemPhoto(val mediaItem: MediaItem) : MediaItemListAction()
    data class NavigateToMediaItemVideo(val mediaItem: MediaItem) : MediaItemListAction()
}
