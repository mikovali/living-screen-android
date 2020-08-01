package com.sensorfields.livingscreen.android.domain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sensorfields.livingscreen.android.domain.data.dto.AlbumDto

@Database(entities = [AlbumDto::class], version = 1)
abstract class ApplicationDb : RoomDatabase() {

    abstract fun albumDao(): AlbumDao
}
