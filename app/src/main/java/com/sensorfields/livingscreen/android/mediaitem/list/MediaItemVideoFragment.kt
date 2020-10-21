package com.sensorfields.livingscreen.android.mediaitem.list

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.MediaItem as ExoPlayerMediaItem

class MediaItemVideoFragment : VideoSupportFragment() {

    private val args by navArgs<MediaItemVideoFragmentArgs>()

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerAdapter: LeanbackPlayerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerAdapter = LeanbackPlayerAdapter(requireContext(), player, UPDATE_PERIODS_MS)
        PlaybackTransportControlGlue(requireContext(), playerAdapter).apply {
            host = VideoSupportFragmentGlueHost(this@MediaItemVideoFragment)
        }

        playerAdapter.setPlaybackPreparer {
            player.setMediaItem(ExoPlayerMediaItem.fromUri("${args.mediaItem.baseUrl}=dv"))
            player.prepare()
        }
        playerAdapter.play()
    }

    override fun onDestroyView() {
        player.release()
        super.onDestroyView()
    }
}

private const val UPDATE_PERIODS_MS = 300
