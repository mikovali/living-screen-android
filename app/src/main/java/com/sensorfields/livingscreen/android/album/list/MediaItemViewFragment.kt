package com.sensorfields.livingscreen.android.album.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.leanback.media.PlaybackGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemViewFragment : Fragment(R.layout.media_item_view_fragment) {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        onState(viewModel.getMediaItemViewState(args.index))
    }

    private fun onState(state: MediaItemViewState) {
        val fragment = when (state.current.type) {
            MediaItem.Type.Photo -> MediaItemPhotoViewFragment()
            MediaItem.Type.Video -> MediaItemVideoViewFragment()
        }.apply {
            arguments = args.toBundle()
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.containerView, fragment)
            .commit()
    }
}

class PlaybackControlGlue<T : PlayerAdapter>(
    context: Context,
    adapter: T,
    host: PlaybackGlueHost,
    private val state: MediaItemViewState,
    private val skipPreviousActionClicked: (MediaItemGridState.Item) -> Unit,
    private val skipNextActionClicked: (MediaItemGridState.Item) -> Unit,
    private val moreActionsClicked: (MediaItemGridState.Item) -> Unit
) : PlaybackTransportControlGlue<T>(
    context, adapter
) {
    private val skipPreviousAction = PlaybackControlsRow.SkipPreviousAction(context)
    private val skipNextAction = PlaybackControlsRow.SkipNextAction(context)
    private val moreActions = PlaybackControlsRow.MoreActions(context)

    init {
        this.host = host
        title = state.current.fileName
        playWhenPrepared()
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        if (state.current.type is MediaItem.Type.Video) {
            super.onCreatePrimaryActions(primaryActionsAdapter)
        }
        if (state.previous != null) primaryActionsAdapter.add(skipPreviousAction)
        if (state.next != null) primaryActionsAdapter.add(skipNextAction)
        primaryActionsAdapter.add(moreActions)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            skipPreviousAction -> state.previous?.apply { skipPreviousActionClicked(this) }
            skipNextAction -> state.next?.apply { skipNextActionClicked(this) }
            moreActions -> moreActionsClicked(state.current)
            else -> super.onActionClicked(action)
        }
    }
}
