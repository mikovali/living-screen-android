package com.sensorfields.livingscreen.android.mediaitem.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.usecase.IsGoogleAccountConnectedUseCase
import com.sensorfields.livingscreen.android.domain.usecase.ObserveMediaItemsUseCase
import com.sensorfields.livingscreen.android.model.MediaItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaItemListViewModel @Inject constructor(
    private val isGoogleAccountConnectedUseCase: IsGoogleAccountConnectedUseCase,
    private val observeMediaItemsUseCase: ObserveMediaItemsUseCase
) : ViewModel() {

    private val _action = ActionLiveData<MediaItemListAction>()
    val action: LiveData<MediaItemListAction> = _action

    val listUpdateCallback = FlowListUpdateCallback()
    val differ = AsyncPagingDataDiffer(
        diffCallback = DiffUtilCallback,
        updateCallback = listUpdateCallback
    )

    init {
        isGoogleAccountConnected()
        observeMediaItems()
    }

    fun onMediaItemClicked(mediaItem: MediaItem) {
        val action = when (mediaItem.type) {
            MediaItem.Type.Photo -> MediaItemListAction.NavigateToMediaItemPhoto(mediaItem)
            MediaItem.Type.Video -> MediaItemListAction.NavigateToMediaItemVideo(mediaItem)
        }
        _action.postValue(action)
    }

    fun onPreviousClicked(mediaItem: MediaItem) {
        val index = differ.snapshot().indexOf(mediaItem)
        if (index > 0) {
            differ.getItem(index - 1)?.let { previous ->
                onMediaItemClicked(previous)
            }
        }
    }

    fun onNextClicked(mediaItem: MediaItem) {
        val index = differ.snapshot().indexOf(mediaItem)
        if (index + 1 < differ.itemCount) {
            differ.getItem(index + 1)?.let { next ->
                onMediaItemClicked(next)
            }
        }
    }

    private fun isGoogleAccountConnected() {
        if (!isGoogleAccountConnectedUseCase()) {
            _action.postValue(MediaItemListAction.NavigateToAccountCreate)
        }
    }

    private fun observeMediaItems() = viewModelScope.launch {
        observeMediaItemsUseCase().collect { differ.submitData(it) }
    }
}

private object DiffUtilCallback : DiffUtil.ItemCallback<MediaItem>() {

    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem == newItem
    }
}

class FlowListUpdateCallback : ListUpdateCallback {

    private val eventChannel = Channel<Event>(capacity = Channel.UNLIMITED)
    val events: Flow<Event> = flow { for (event in eventChannel) emit(event) }

    override fun onInserted(position: Int, count: Int) {
        eventChannel.offer(Event.OnInserted(position, count))
    }

    override fun onRemoved(position: Int, count: Int) {
        eventChannel.offer(Event.OnRemoved(position, count))
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        eventChannel.offer(Event.OnMoved(fromPosition, toPosition))
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        eventChannel.offer(Event.OnChanged(position, count, payload))
    }

    sealed class Event {
        data class OnInserted(val position: Int, val count: Int) : Event()
        data class OnRemoved(val position: Int, val count: Int) : Event()
        data class OnMoved(val fromPosition: Int, val toPosition: Int) : Event()
        data class OnChanged(val position: Int, val count: Int, val payload: Any?) : Event()
    }
}
