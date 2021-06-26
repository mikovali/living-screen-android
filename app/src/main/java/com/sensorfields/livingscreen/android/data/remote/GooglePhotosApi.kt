package com.sensorfields.livingscreen.android.data.remote

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Tasks
import com.sensorfields.livingscreen.android.HTTP_UNAUTHORIZED
import com.sensorfields.livingscreen.android.data.dto.AlbumDto
import com.sensorfields.livingscreen.android.data.dto.MediaItemDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import timber.log.Timber
import javax.inject.Inject

interface GooglePhotosApi {

    @GET("albums")
    suspend fun getAlbums(): GetAlbumsResponse

    @GET("sharedAlbums")
    suspend fun getSharedAlbums(): GetSharedAlbumsResponse

    @POST("./mediaItems:search")
    suspend fun searchMediaItems(@Body request: SearchMediaItemsRequest): SearchMediaItemsResponse
}

@Serializable
data class GetAlbumsResponse(val albums: List<AlbumDto> = emptyList())

@Serializable
data class GetSharedAlbumsResponse(val sharedAlbums: List<AlbumDto>)

@Serializable
data class SearchMediaItemsRequest(val pageSize: Int, val pageToken: String?)

@Serializable
data class SearchMediaItemsResponse(
    val mediaItems: List<MediaItemDto>,
    val nextPageToken: String? = null
)

class GooglePhotosAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleSignInOptions: GoogleSignInOptions
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken() ?: return chain.proceed(chain.request())

        val request = chain.request().setAuthorizationHeader(token)
        val response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED) {
            clearToken(token)
            val newToken = getToken()
            if (newToken != null) {
                val newResponse = chain.proceed(request.setAuthorizationHeader(newToken))
                if (newResponse.code == HTTP_UNAUTHORIZED) {
                    Tasks.await(GoogleSignIn.getClient(context, googleSignInOptions).signOut())
                }
                return newResponse
            }
        }

        return response
    }

    private fun getToken(): String? {
        return GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
            try {
                GoogleAuthUtil.getToken(
                    context,
                    googleAccount.account,
                    "oauth2:${googleAccount.requestedScopes.joinToString(" ") { it.scopeUri }}"
                )
            } catch (e: Exception) {
                Timber.w(e, "Get Google Auth token")
                null
            }
        }
    }

    private fun clearToken(token: String) {
        GoogleAuthUtil.clearToken(context, token)
    }
}

private fun Request.setAuthorizationHeader(token: String): Request {
    return newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
}
