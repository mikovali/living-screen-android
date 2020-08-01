package com.sensorfields.livingscreen.android.album.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.AccountRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AlbumListViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _action = ActionLiveData<AlbumListAction>()
    val action: LiveData<AlbumListAction> = _action

    init {
        observeAccount()
    }

    private fun observeAccount() {
        accountRepository.observeAccount()
            .onEach { account ->
                if (account == null) _action.postValue(AlbumListAction.NavigateToAccountCreate)
            }
            .launchIn(viewModelScope)
    }
}
