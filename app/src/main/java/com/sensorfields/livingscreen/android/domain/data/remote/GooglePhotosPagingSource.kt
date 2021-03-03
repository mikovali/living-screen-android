package com.sensorfields.livingscreen.android.domain.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.domain.data.dto.toModels

class GooglePhotosPagingSource(
    private val googlePhotosApi: GooglePhotosApi
) : PagingSource<String, MediaItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MediaItem> {
        return try {
            val response = googlePhotosApi.searchMediaItems(
                SearchMediaItemsRequest(
                    pageSize = params.loadSize,
                    pageToken = params.key
                )
            )
            LoadResult.Page(
                data = response.mediaItems.toModels(),
                prevKey = null,
                nextKey = response.nextPageToken
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, MediaItem>): String? = null
}
