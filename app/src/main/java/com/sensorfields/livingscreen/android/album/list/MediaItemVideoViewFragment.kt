package com.sensorfields.livingscreen.android.album.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import com.sensorfields.livingscreen.android.viewState
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

class MediaItemVideoViewFragment : VideoSupportFragment() {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val mediaSourceFactory = ProgressiveMediaSource.Factory(
        OkHttpDataSourceFactory(OkHttpClient(), null)
    )

    private val player by viewState(
        create = { view -> SimpleExoPlayer.Builder(view.context).build() },
        destroy = { release() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        onState(viewModel.getMediaItemViewState(args.index))
    }

    private fun onState(state: MediaItemViewState) {
        PlaybackControlGlue(
            requireContext(),
            LeanbackPlayerAdapter(requireContext(), player, UPDATE_PERIODS_MS),
            VideoSupportFragmentGlueHost(this),
            state,
            skipPreviousActionClicked = ::onPreviousClicked,
            skipNextActionClicked = ::onNextClicked,
            moreActionsClicked = ::onDetailsClicked
        ).apply {
            isSeekEnabled = true
            playerAdapter.setPlaybackPreparer {
                player.prepare(
                    mediaSourceFactory.createMediaSource("${state.baseUrl}=dv".toUri())
                )
            }
            play()
        }
    }

    private fun onDetailsClicked() {
        viewModel.onDetailsClicked(args.index)
    }

    private fun onPreviousClicked() {
        viewModel.onPreviousClicked(args.index)
    }

    private fun onNextClicked() {
        viewModel.onNextClicked(args.index)
    }
}

private const val UPDATE_PERIODS_MS = 300

class PlaybackControlGlue<T : PlayerAdapter>(
    context: Context,
    adapter: T,
    host: PlaybackGlueHost,
    private val state: MediaItemViewState,
    private val skipPreviousActionClicked: () -> Unit,
    private val skipNextActionClicked: () -> Unit,
    private val moreActionsClicked: () -> Unit
) : PlaybackTransportControlGlue<T>(
    context, adapter
) {
    private val skipPreviousAction = PlaybackControlsRow.SkipPreviousAction(context)
    private val skipNextAction = PlaybackControlsRow.SkipNextAction(context)
    private val moreActions = PlaybackControlsRow.MoreActions(context)

    init {
        this.host = host
        title = state.fileName
        playWhenPrepared()
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        if (state.isPreviousVisible) primaryActionsAdapter.add(skipPreviousAction)
        if (state.isNextVisible) primaryActionsAdapter.add(skipNextAction)
        primaryActionsAdapter.add(moreActions)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            skipPreviousAction -> skipPreviousActionClicked()
            skipNextAction -> skipNextActionClicked()
            moreActions -> moreActionsClicked()
            else -> super.onActionClicked(action)
        }
    }
}
