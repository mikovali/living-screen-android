package com.sensorfields.livingscreen.android.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sensorfields.livingscreen.android.domain.data.remote.GooglePhotosApi
import com.sensorfields.livingscreen.android.domain.data.remote.GooglePhotosPagingSource
import com.sensorfields.livingscreen.android.model.MediaItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMediaItemsUseCase @Inject constructor(private val googlePhotosApi: GooglePhotosApi) {

    operator fun invoke(): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 25),
            pagingSourceFactory = { GooglePhotosPagingSource(googlePhotosApi) }
        ).flow
    }
}
