package com.sensorfields.livingscreen.android.domain.data.remote

import com.sensorfields.livingscreen.android.domain.data.dto.AlbumDto
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header

interface AlbumApi {

    // TODO headers with interceptor
    @GET("albums")
    suspend fun getAlbums(@Header("Authorization") authorization: String): GetAlbumsResponse
}

@Serializable
data class GetAlbumsResponse(val albums: List<AlbumDto>)
