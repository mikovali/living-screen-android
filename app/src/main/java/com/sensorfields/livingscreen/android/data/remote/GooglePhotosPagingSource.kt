package com.sensorfields.livingscreen.android.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sensorfields.livingscreen.android.mapping.toModels
import com.sensorfields.livingscreen.android.model.MediaItem

class GooglePhotosPagingSource(
    private val googlePhotosApi: GooglePhotosApi,
    private val albumId: String?
) : PagingSource<String, MediaItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MediaItem> {
        return try {
            val response = googlePhotosApi.searchMediaItems(
                SearchMediaItemsRequest(
                    albumId = albumId,
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
