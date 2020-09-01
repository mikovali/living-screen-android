package com.sensorfields.livingscreen.android.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaItem(
    val id: String,
    val baseUrl: String,
    val fileName: String
) : Parcelable
