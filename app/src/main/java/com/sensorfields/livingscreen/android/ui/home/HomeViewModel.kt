package com.sensorfields.livingscreen.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.sensorfields.livingscreen.android.model.Album
import com.sensorfields.livingscreen.android.usecase.ObserveAlbumsUseCase
import com.sensorfields.livingscreen.android.usecase.RefreshAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeAlbumsUseCase: ObserveAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: Flow<HomeState> = _state

    init {
        observeAlbums()
        refreshAlbums()
    }

    private fun observeAlbums() = viewModelScope.launch {
        observeAlbumsUseCase().collect { result ->
            _state.value = _state.value.copy(
                albums = result.albums.toItems(),
                sharedAlbums = result.sharedAlbums.toItems()
            )
        }
    }

    private fun refreshAlbums() = viewModelScope.launch {
        when (val result = refreshAlbumsUseCase()) {
            is Either.Right -> {
            }
            is Either.Left -> {
                Timber.e("Refresh albubs: ${result.value}")
            }
        }
    }
}

private fun List<Album>.toItems(): List<HomeState.AlbumItem> = map { album ->
    HomeState.AlbumItem(
        id = album.id,
        title = album.title
    )
}
