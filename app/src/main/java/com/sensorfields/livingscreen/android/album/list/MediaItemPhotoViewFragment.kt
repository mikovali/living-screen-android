package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlayerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoBinding
import com.sensorfields.livingscreen.android.producer
import com.sensorfields.livingscreen.android.viewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemPhotoViewFragment : PlaybackSupportFragment() {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val viewBinding by viewState(
        create = { view ->
            with(view as ViewGroup) {
                MediaItemPhotoBinding.inflate(LayoutInflater.from(context), this, false).apply {
                    this@with.addView(root, 0)
                }
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        hideControlsOverlay(false)
    }

    private fun setupViewModel() {
        onState(viewModel.getMediaItemViewState(args.index))
    }

    private fun onState(state: MediaItemViewState) {
        PlaybackControlGlue(
            requireContext(),
            PhotoAdapter(viewBinding, state.current),
            PlaybackSupportFragmentGlueHost(this),
            state,
            skipPreviousActionClicked = ::navigateToMediaItemView,
            skipNextActionClicked = ::navigateToMediaItemView,
            moreActionsClicked = ::onDetailsClicked
        )
    }

    private fun navigateToMediaItemView(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemView(item.index))
    }

    private fun onDetailsClicked(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemDetails(item.index))
    }
}

private class PhotoAdapter(
    private val viewBinding: MediaItemPhotoBinding,
    private val item: MediaItemGridState.Item
) : PlayerAdapter() {

    private val size by lazy {
        isPrepared
        Point(
            viewBinding.root.resources.displayMetrics.widthPixels,
            viewBinding.root.resources.displayMetrics.heightPixels
        )
    }

    override fun play() {
        viewBinding.imageView.contentDescription = item.fileName
        Glide.with(viewBinding.imageView)
            .load("${item.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
    }

    override fun pause() {
    }
}
