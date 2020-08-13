package com.sensorfields.livingscreen.android.domain.usecase

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Reusable
class RefreshAlbumsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val albumApi: AlbumApi,
    private val albumDao: AlbumDao
) {
    suspend operator fun invoke(): Either<Error, Unit> {
        return try {
            GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
                // TODO should be cached
                val token = GoogleAuthUtil.getToken(
                    context,
                    account.account,
                    "oauth2:${account.requestedScopes.joinToString(" ") { it.scopeUri }}"
                )
                albumDao.replaceAlbums(albumApi.getAlbums("Bearer $token").albums)
            }
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
