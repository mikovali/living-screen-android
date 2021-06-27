package com.sensorfields.livingscreen.android.ui.mediaitem.grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.sensorfields.livingscreen.android.usecase.ObserveMediaItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class MediaItemGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeMediaItemsUseCase: ObserveMediaItemsUseCase
) : ViewModel() {

    private val albumId: String? = savedStateHandle.get("albumId")

    val items = observeMediaItemsUseCase(albumId)
        .mapLatest { it.map { mediaItem -> mediaItem.toItem() } }
        .cachedIn(viewModelScope)
}
