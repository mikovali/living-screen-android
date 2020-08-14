package com.sensorfields.livingscreen.android.domain.data.remote

import com.sensorfields.livingscreen.android.domain.data.dto.AlbumDto
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * TODO headers with interceptor
 */
interface AlbumApi {

    @GET("albums")
    suspend fun getAlbums(@Header("Authorization") authorization: String): GetAlbumsResponse

    @GET("sharedAlbums")
    suspend fun getSharedAlbums(
        @Header("Authorization") authorization: String
    ): GetSharedAlbumsResponse
}

@Serializable
data class GetAlbumsResponse(val albums: List<AlbumDto>)

@Serializable
data class GetSharedAlbumsResponse(val sharedAlbums: List<AlbumDto>)
