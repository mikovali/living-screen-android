package com.sensorfields.livingscreen.android

import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.local.ApplicationDb
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun firebaseApp(@ApplicationContext context: Context): FirebaseApp {
        return FirebaseApp.initializeApp(context)!!
    }

    @Singleton
    @Provides
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth = FirebaseAuth(firebaseApp)
}

@Module
@InstallIn(ApplicationComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun applicationDb(@ApplicationContext context: Context): ApplicationDb {
        return Room.databaseBuilder(context, ApplicationDb::class.java, "application").build()
    }

    @Singleton
    @Provides
    fun albumDao(applicationDb: ApplicationDb): AlbumDao = applicationDb.albumDao()
}

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun retrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun albumApi(retrofit: Retrofit): AlbumApi = retrofit.create()
}
