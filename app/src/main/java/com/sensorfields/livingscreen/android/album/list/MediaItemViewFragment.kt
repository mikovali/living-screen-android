package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

class MediaItemViewFragment : VideoSupportFragment() {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private lateinit var player: SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaSourceFactory = ProgressiveMediaSource.Factory(
            OkHttpDataSourceFactory(OkHttpClient(), null)
        )
        player = SimpleExoPlayer.Builder(requireContext()).build()

        viewModel.getMediaItemViewState(args.index).observe(viewLifecycleOwner) { state ->
            PlayerGlue(this, player, mediaSourceFactory, state.current) {
                findNavController().navigate(
                    MediaItemViewFragmentDirections.mediaItemDetails(state.current.index)
                )
            }
        }
    }

    override fun onDestroyView() {
        player.release()
        super.onDestroyView()
    }
}

private class PlayerGlue(
    fragment: VideoSupportFragment,
    player: SimpleExoPlayer,
    mediaSourceFactory: MediaSourceFactory,
    item: MediaItemGridState.Item,
    private val moreActionsClicked: () -> Unit
) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
    fragment.requireContext(),
    LeanbackPlayerAdapter(fragment.requireContext(), player, UPDATE_PERIODS_MS)
) {
    private val moreActions = PlaybackControlsRow.MoreActions(context)

    init {
        host = VideoSupportFragmentGlueHost(fragment)
        title = item.fileName
        isSeekEnabled = true
        playerAdapter.setPlaybackPreparer {
            player.prepare(
                mediaSourceFactory.createMediaSource("${item.baseUrl}=dv".toUri())
            )
        }
        play()
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        primaryActionsAdapter.add(moreActions)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            moreActions -> moreActionsClicked()
            else -> super.onActionClicked(action)
        }
    }
}

private const val UPDATE_PERIODS_MS = 300
