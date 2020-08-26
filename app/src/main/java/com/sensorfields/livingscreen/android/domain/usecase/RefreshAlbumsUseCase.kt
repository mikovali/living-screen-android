package com.sensorfields.livingscreen.android.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.remote.GooglePhotosApi
import dagger.Reusable
import javax.inject.Inject

@Reusable
class RefreshAlbumsUseCase @Inject constructor(
    private val googlePhotosApi: GooglePhotosApi,
    private val albumDao: AlbumDao
) {
    suspend operator fun invoke(): Either<Error, Unit> {
        return try {
            val albums = googlePhotosApi.getAlbums().albums
            val sharedAlbums = googlePhotosApi.getSharedAlbums().sharedAlbums
                .filter { albums.find { album -> album.id == it.id } == null }
                .map { it.copy(isShared = true) }
            albumDao.replaceAlbums(albums + sharedAlbums)
            Unit.right()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Error.General.left()
        }
    }

    sealed class Error {
        object General : Error()
    }
}
