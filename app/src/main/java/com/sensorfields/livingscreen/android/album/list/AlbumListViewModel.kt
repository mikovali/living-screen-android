package com.sensorfields.livingscreen.android.album.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.usecase.IsGoogleAccountConnectedUseCase
import com.sensorfields.livingscreen.android.domain.usecase.ObserveAlbumsUseCase
import com.sensorfields.livingscreen.android.domain.usecase.ObserveMediaItemsUseCase
import com.sensorfields.livingscreen.android.domain.usecase.RefreshAlbumsUseCase
import com.sensorfields.livingscreen.android.reduceValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumListViewModel @Inject constructor(
    private val isGoogleAccountConnectedUseCase: IsGoogleAccountConnectedUseCase,
    private val observeAlbumsUseCase: ObserveAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
    observeMediaItemsUseCase: ObserveMediaItemsUseCase
) : ViewModel() {

    private val _state = MutableLiveData(AlbumListState())
    val state: LiveData<AlbumListState> = _state

    private val _action = ActionLiveData<AlbumListAction>()
    val action: LiveData<AlbumListAction> = _action

    val mediaItemGridState: LiveData<MediaItemGridState> = observeMediaItemsUseCase(null)
        .map { mediaItems ->
            MediaItemGridState(
                items = mediaItems.mapIndexed { index, mediaItem ->
                    MediaItemGridState.Item(
                        index = index,
                        type = mediaItem.type,
                        baseUrl = mediaItem.baseUrl,
                        fileName = mediaItem.fileName
                    )
                }
            )
        }
        .asLiveData(viewModelScope.coroutineContext)

    init {
        isGoogleAccountConnected()
        observeAlbums()
        refreshAlbums()
    }

    fun getMediaItemViewState(index: Int): MediaItemViewState {
        val state = mediaItemGridState.value!!
        return MediaItemViewState(
            current = state.items[index],
            previous = state.items.getOrNull(index - 1),
            next = state.items.getOrNull(index + 1)
        )
    }

    private fun isGoogleAccountConnected() {
        if (!isGoogleAccountConnectedUseCase()) {
            _action.postValue(AlbumListAction.NavigateToAccountCreate)
        }
    }

    private fun observeAlbums() {
        observeAlbumsUseCase()
            .onEach { result ->
                _state.reduceValue {
                    copy(
                        albums = result.albums,
                        sharedAlbums = result.sharedAlbums
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshAlbums() = viewModelScope.launch {
        when (refreshAlbumsUseCase()) {
            is Either.Left -> {
                // TODO error
            }
        }
    }
}
