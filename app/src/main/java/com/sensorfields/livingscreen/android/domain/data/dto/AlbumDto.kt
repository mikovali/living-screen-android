package com.sensorfields.livingscreen.android.domain.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumDto(
    @PrimaryKey val id: String,
    val title: String
)
