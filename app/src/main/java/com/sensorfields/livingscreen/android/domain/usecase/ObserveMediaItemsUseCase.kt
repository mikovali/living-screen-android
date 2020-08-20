package com.sensorfields.livingscreen.android.domain.usecase

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sensorfields.livingscreen.android.domain.Album
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.domain.data.dto.toModels
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveMediaItemsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val albumApi: AlbumApi
) {
    operator fun invoke(album: Album?): Flow<List<MediaItem>> = flow {
        try {
            GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
                // TODO should be cached
                val token = "Bearer " + GoogleAuthUtil.getToken(
                    context,
                    account.account,
                    "oauth2:${account.requestedScopes.joinToString(" ") { it.scopeUri }}"
                )
                emit(
                    albumApi.searchMediaItems(token)
                        .mediaItems.toModels()
                )
            }
        } catch (e: Exception) {
            Log.e("ObserveMediaItemsUseCas", "error", e)
        }
    }
}
