package com.sensorfields.livingscreen.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    val id: String,
    val type: Type,
    val baseUrl: String,
    val filename: String
) : Parcelable {

    sealed class Type : Parcelable {

        @Parcelize
        object Photo : Type()

        @Parcelize
        object Video : Type()
    }
}
