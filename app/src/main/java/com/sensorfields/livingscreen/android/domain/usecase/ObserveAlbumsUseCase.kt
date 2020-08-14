package com.sensorfields.livingscreen.android.domain.usecase

import com.sensorfields.livingscreen.android.domain.Album
import com.sensorfields.livingscreen.android.domain.data.dto.toModels
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class ObserveAlbumsUseCase @Inject constructor(private val albumDao: AlbumDao) {

    operator fun invoke(): Flow<Result> {
        return albumDao.observeAlbums().map { albums ->
            Result(
                albums = albums.filter { !it.isShared }.toModels(),
                sharedAlbums = albums.filter { it.isShared }.toModels()
            )
        }
    }

    data class Result(
        val albums: List<Album>,
        val sharedAlbums: List<Album>
    )
}
