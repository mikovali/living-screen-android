package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.navigation.fragment.findNavController
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
            skipPreviousActionClicked = ::navigateToMediaItemView,
            skipNextActionClicked = ::navigateToMediaItemView,
            moreActionsClicked = ::onDetailsClicked
        ).apply {
            isSeekEnabled = true
            playerAdapter.setPlaybackPreparer {
                player.prepare(
                    mediaSourceFactory.createMediaSource("${state.current.baseUrl}=dv".toUri())
                )
            }
            play()
        }
    }

    private fun navigateToMediaItemView(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemView(item.index))
    }

    private fun onDetailsClicked(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemDetails(item.index))
    }
}

private const val UPDATE_PERIODS_MS = 300
