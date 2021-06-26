package com.sensorfields.livingscreen.android.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class AlbumDto(
    @PrimaryKey val id: String,
    val title: String = "",
    val isShared: Boolean = false
)
