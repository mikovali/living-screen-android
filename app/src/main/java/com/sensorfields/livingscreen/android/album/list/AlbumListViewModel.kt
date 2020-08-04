package com.sensorfields.livingscreen.android.album.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.AccountRepository
import com.sensorfields.livingscreen.android.domain.AlbumRepository
import com.sensorfields.livingscreen.android.reduceValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumListViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val albumRepository: AlbumRepository
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
        accountRepository.observeAccount()
            .onEach { account ->
                if (account == null) _action.postValue(AlbumListAction.NavigateToAccountCreate)
            }
            .launchIn(viewModelScope)
    }

    private fun observeAlbums() {
        albumRepository.observeAlbums()
            .onEach { albums ->
                _state.reduceValue { copy(albums = albums) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshAlbums() {
        viewModelScope.launch {
            try {
                albumRepository.refreshAlbums()
            } catch (e: Exception) {
                Log.e("AlbumListViewModel", "refreshAlbums", e)
            }
        }
    }
}
