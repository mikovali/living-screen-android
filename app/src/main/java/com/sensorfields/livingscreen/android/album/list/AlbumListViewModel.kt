package com.sensorfields.livingscreen.android.album.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.usecase.ObserveAccountUseCase
import com.sensorfields.livingscreen.android.domain.usecase.ObserveAlbumsUseCase
import com.sensorfields.livingscreen.android.domain.usecase.RefreshAlbumsUseCase
import com.sensorfields.livingscreen.android.reduceValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumListViewModel @Inject constructor(
    private val observeAccountUseCase: ObserveAccountUseCase,
    private val observeAlbumsUseCase: ObserveAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AlbumListState>(AlbumListState())
    val state: LiveData<AlbumListState> = _state

    private val _action = ActionLiveData<AlbumListAction>()
    val action: LiveData<AlbumListAction> = _action

    init {
        observeAccount()
        observeAlbums()
        refreshAlbums()
    }

    private fun observeAccount() {
        observeAccountUseCase()
            .onEach { account ->
                if (account == null) _action.postValue(AlbumListAction.NavigateToAccountCreate)
            }
            .launchIn(viewModelScope)
    }

    private fun observeAlbums() {
        observeAlbumsUseCase()
            .onEach { albums ->
                _state.reduceValue { copy(albums = albums) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshAlbums() {
        viewModelScope.launch(Dispatchers.IO) { // TODO remove dispatcher when interceptor
            when (refreshAlbumsUseCase()) {
                is Either.Left -> {
                    // TODO error
                }
            }
        }
    }
}
