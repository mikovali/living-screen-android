package com.sensorfields.livingscreen.android.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.sensorfields.livingscreen.android.data.local.AlbumDao
import com.sensorfields.livingscreen.android.data.remote.GooglePhotosApi
import dagger.Reusable
import timber.log.Timber
import javax.inject.Inject

@Reusable
class RefreshAlbumsUseCase @Inject constructor(
    private val googlePhotosApi: GooglePhotosApi,
    private val albumDao: AlbumDao
) {
    suspend operator fun invoke(): Either<Error, Unit> {
        return try {
            val albums = googlePhotosApi.getAlbums().albums ?: emptyList()
            val sharedAlbums = googlePhotosApi.getSharedAlbums().sharedAlbums
                .filter { albums.find { album -> album.id == it.id } == null }
                .map { it.copy(isShared = true) }
            albumDao.replaceAlbums(albums + sharedAlbums)
            Unit.right()
        } catch (e: Exception) {
            Timber.w(e, "Refresh albums")
            Error.General.left()
        }
    }

    sealed class Error {
        object General : Error()
    }
}
