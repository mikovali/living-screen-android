package com.sensorfields.livingscreen.android.domain

import com.google.firebase.auth.FirebaseAuth
import com.sensorfields.livingscreen.android.await
import com.sensorfields.livingscreen.android.domain.data.dto.toModels
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth
) {
    fun observeAlbums(): Flow<List<Album>> {
        return albumDao.observeAlbums().map { it.toModels() }
    }

    suspend fun refreshAlbums() {
        albumDao.replaceAlbums(
            albumApi.getAlbums("Bearer ${firebaseAuth.getAccessToken(true).await().token}").albums
        )
    }
}
