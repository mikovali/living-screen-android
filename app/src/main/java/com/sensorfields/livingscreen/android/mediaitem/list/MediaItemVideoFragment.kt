package com.sensorfields.livingscreen.android.mediaitem.list

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import javax.inject.Inject
import javax.inject.Provider
import com.google.android.exoplayer2.MediaItem as ExoPlayerMediaItem

class MediaItemVideoFragment : VideoSupportFragment() {

    private val args by navArgs<MediaItemVideoFragmentArgs>()

    @Inject
    lateinit var factory: Provider<MediaItemListViewModel>

    private val viewModel by navGraphViewModels<MediaItemListViewModel>(
        navGraphId = R.id.mediaItemListFragment,
        factoryProducer = { producer { factory.get() } }
    )

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerAdapter: LeanbackPlayerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerAdapter = LeanbackPlayerAdapter(requireContext(), player, UPDATE_PERIODS_MS)
        object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireContext(),
            playerAdapter
        ) {
            init {
                host = VideoSupportFragmentGlueHost(this@MediaItemVideoFragment)
            }

            override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
                super.onCreatePrimaryActions(primaryActionsAdapter)
                primaryActionsAdapter.add(
                    PlaybackControlsRow.SkipPreviousAction(requireContext()).apply {
                        id = ACTION_PREVIOUS
                    }
                )
                primaryActionsAdapter.add(
                    PlaybackControlsRow.SkipNextAction(requireContext()).apply {
                        id = ACTION_NEXT
                    }
                )
            }

            override fun onActionClicked(action: Action) {
                when (action.id) {
                    ACTION_PREVIOUS -> viewModel.onPreviousClicked(args.mediaItem)
                    ACTION_NEXT -> viewModel.onNextClicked(args.mediaItem)
                    else -> super.onActionClicked(action)
                }
            }
        }

        playerAdapter.setPlaybackPreparer {
            player.setMediaItem(ExoPlayerMediaItem.fromUri("${args.mediaItem.baseUrl}=dv"))
            player.prepare()
        }
        playerAdapter.play()

        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    override fun onDestroyView() {
        player.release()
        super.onDestroyView()
    }

    private fun onAction(action: MediaItemListAction) {
        when (action) {
            is MediaItemListAction.NavigateToMediaItemPhoto -> {
                findNavController().navigate(
                    MediaItemVideoFragmentDirections.mediaItemPhoto(action.mediaItem)
                )
            }
            is MediaItemListAction.NavigateToMediaItemVideo -> {
                findNavController().navigate(
                    MediaItemVideoFragmentDirections.mediaItemVideo(action.mediaItem)
                )
            }
            else -> {
            }
        }
    }
}

private const val UPDATE_PERIODS_MS = 300

private const val ACTION_PREVIOUS = 1L
private const val ACTION_NEXT = 2L
