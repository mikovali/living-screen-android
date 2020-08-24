package com.sensorfields.livingscreen.android.domain

import android.net.Uri

data class MediaItem(
    val id: String,
    val thumbnail: Uri,
    val fileName: String
)
