package com.sensorfields.livingscreen.android.domain

import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi
)
