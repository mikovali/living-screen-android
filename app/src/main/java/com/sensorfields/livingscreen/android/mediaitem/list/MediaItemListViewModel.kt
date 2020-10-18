package com.sensorfields.livingscreen.android.mediaitem.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.domain.usecase.IsGoogleAccountConnectedUseCase
import com.sensorfields.livingscreen.android.domain.usecase.ObserveMediaItemsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaItemListViewModel @Inject constructor(
    private val isGoogleAccountConnectedUseCase: IsGoogleAccountConnectedUseCase,
    observeMediaItemsUseCase: ObserveMediaItemsUseCase
) : ViewModel() {

    private val _action = ActionLiveData<MediaItemListAction>()
    val action: LiveData<MediaItemListAction> = _action

    val mediaItemsPagingData: Flow<PagingData<MediaItem>> = observeMediaItemsUseCase()
        .cachedIn(viewModelScope)

    init {
        isGoogleAccountConnected()
    }

    private fun isGoogleAccountConnected() {
        if (!isGoogleAccountConnectedUseCase()) {
            _action.postValue(MediaItemListAction.NavigateToAccountCreate)
        }
    }
}
