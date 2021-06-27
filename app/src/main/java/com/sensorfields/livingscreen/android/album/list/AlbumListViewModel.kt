package com.sensorfields.livingscreen.android.album.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.usecase.IsGoogleAccountConnectedUseCase
import com.sensorfields.livingscreen.android.model.MediaItem
import timber.log.Timber
import javax.inject.Inject

class AlbumListViewModel @Inject constructor(
    private val isGoogleAccountConnectedUseCase: IsGoogleAccountConnectedUseCase,
//    private val observeAlbumsUseCase: ObserveAlbumsUseCase,
//    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
//    private val observeMediaItemsUseCase: ObserveMediaItemsUseCase
) : ViewModel() {

    private val _state = MutableLiveData(AlbumListState())
    val state: LiveData<AlbumListState> = _state

    private val _action = ActionLiveData<AlbumListAction>()
    val action: LiveData<AlbumListAction> = _action

//    val mediaItemsData = observeMediaItemsUseCase()
//        .map {
//            it.map { mediaItem ->
//                MediaItemGridState.Item(
//                    id = mediaItem.id,
//                    index = 0,
//                    type = mediaItem.type,
//                    baseUrl = mediaItem.baseUrl,
//                    fileName = mediaItem.fileName
//                )
//            }
//
//
//        }
//        .cachedIn(viewModelScope)

//    private val _mediaItemGridState = MutableLiveData(MediaItemGridState())
//    val mediaItemGridState: LiveData<MediaItemGridState> = _mediaItemGridState

//    val mediaItemGridState: LiveData<MediaItemGridState> = observeMediaItemsUseCase(null)
//        .map { mediaItems ->
//            MediaItemGridState(
//                items = mediaItems.mapIndexed { index, mediaItem ->
//                    MediaItemGridState.Item(
//                        index = index,
//                        type = mediaItem.type,
//                        baseUrl = mediaItem.baseUrl,
//                        fileName = mediaItem.fileName
//                    )
//                }
//            )
//        }
//        .asLiveData(viewModelScope.coroutineContext)

    init {
        isGoogleAccountConnected()
//        observeAlbums()
//        refreshAlbums()
    }

    fun getMediaItemViewState(index: Int): MediaItemViewState {
        val items = listOf<MediaItem>()
        val item = items[index]
        return MediaItemViewState(
            type = item.type,
            baseUrl = item.baseUrl,
            fileName = item.filename,
            isPreviousVisible = index > 0,
            isNextVisible = index < items.size
        )
    }

    fun onDetailsClicked(index: Int) {
        Timber.d("onDetailsClicked: $index")
//        mediaItemGridState.value?.items?.getOrNull(index)?.let {
//            _action.postValue(AlbumListAction.NavigateToMediaItemDetails(it.index))
//        }
    }

    fun onPreviousClicked(index: Int) {
        Timber.d("onPreviousClicked: $index")
//        mediaItemGridState.value?.items?.getOrNull(index - 1)?.let {
//            _action.postValue(AlbumListAction.NavigateToMediaItemView(it.index))
//        }
    }

    fun onNextClicked(index: Int) {
        Timber.d("onNextClicked: $index")
//        mediaItemGridState.value?.items?.getOrNull(index + 1)?.let {
//            _action.postValue(AlbumListAction.NavigateToMediaItemView(it.index))
//        }
    }

    private fun isGoogleAccountConnected() {
        if (!isGoogleAccountConnectedUseCase()) {
            _action.postValue(AlbumListAction.NavigateToAccountCreate)
        }
    }

//    private fun observeAlbums() {
//        observeAlbumsUseCase()
//            .onEach { result ->
//                _state.reduceValue {
//                    copy(
//                        albums = result.albums,
//                        sharedAlbums = result.sharedAlbums
//                    )
//                }
//            }
//            .launchIn(viewModelScope)
//    }
//
//    private fun refreshAlbums() = viewModelScope.launch {
//        when (refreshAlbumsUseCase()) {
//            is Either.Left -> {
//                // TODO error
//            }
//        }
//    }
}
