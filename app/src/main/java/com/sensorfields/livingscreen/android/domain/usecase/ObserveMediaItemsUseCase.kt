package com.sensorfields.livingscreen.android.domain.usecase

import android.util.Log
import com.sensorfields.livingscreen.android.domain.Album
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.domain.data.dto.toModels
import com.sensorfields.livingscreen.android.domain.data.remote.GooglePhotosApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveMediaItemsUseCase @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    operator fun invoke(album: Album?): Flow<List<MediaItem>> = flow {
        try {
            emit(googlePhotosApi.searchMediaItems().mediaItems.toModels())
        } catch (e: Exception) {
            Log.e("ObserveMediaItemsUseCas", "error", e)
        }
    }
}
