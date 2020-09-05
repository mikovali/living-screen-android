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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.sensorfields.livingscreen.android.domain.MediaItem
import okhttp3.OkHttpClient

class MediaItemViewFragment : VideoSupportFragment() {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    private lateinit var player: SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaSourceFactory = ProgressiveMediaSource.Factory(
            OkHttpDataSourceFactory(OkHttpClient(), null)
        )
        player = SimpleExoPlayer.Builder(requireContext()).build()

        PlayerGlue(this, player, mediaSourceFactory, args.mediaItem) {
            findNavController().navigate(
                MediaItemViewFragmentDirections.mediaItemDetails(args.mediaItem)
            )
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
    mediaItem: MediaItem,
    private val moreActionsClicked: () -> Unit
) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
    fragment.requireContext(),
    LeanbackPlayerAdapter(fragment.requireContext(), player, UPDATE_PERIODS_MS)
) {
    private val moreActions = PlaybackControlsRow.MoreActions(context)

    init {
        host = VideoSupportFragmentGlueHost(fragment)
        title = mediaItem.fileName
        isSeekEnabled = true
        playerAdapter.setPlaybackPreparer {
            player.prepare(
                mediaSourceFactory.createMediaSource("${mediaItem.baseUrl}=dv".toUri())
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
