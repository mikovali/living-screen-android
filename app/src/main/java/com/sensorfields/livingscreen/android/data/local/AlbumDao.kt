package com.sensorfields.livingscreen.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sensorfields.livingscreen.android.data.dto.AlbumDto
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao {

    @Query("SELECT * FROM AlbumDto")
    abstract fun observeAlbums(): Flow<List<AlbumDto>>

    @Transaction
    open suspend fun replaceAlbums(albums: List<AlbumDto>) {
        clearAlbums()
        insertAlbums(albums)
    }

    @Query("DELETE FROM AlbumDto")
    protected abstract suspend fun clearAlbums()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAlbums(albums: List<AlbumDto>)
}
