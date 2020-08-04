package com.sensorfields.livingscreen.android.domain

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sensorfields.livingscreen.android.domain.data.dto.toModels
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context, // TODO should be removed
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi
) {
    fun observeAlbums(): Flow<List<Album>> {
        return albumDao.observeAlbums().map { it.toModels() }
    }

    suspend fun refreshAlbums() {
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            // TODO should be cached
            val token = GoogleAuthUtil.getToken(
                context,
                account.account,
                "oauth2:${account.requestedScopes.joinToString(" ") { it.scopeUri }}"
            )
            albumDao.replaceAlbums(albumApi.getAlbums("Bearer $token").albums)
        }
    }
}
