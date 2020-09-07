package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemImageViewFragmentBinding
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.onViewCreated
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemImageViewFragment : Fragment(R.layout.media_item_image_view_fragment) {

    private val args by navArgs<MediaItemImageViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val viewBinding by onViewCreated { MediaItemImageViewFragmentBinding.bind(it) }

    private val size by lazy {
        Point(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
    }

    private var state: MediaItemViewState? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        viewBinding.previousButton.setOnClickListener { onPreviousButtonClicked() }
        viewBinding.nextButton.setOnClickListener { onNextButtonClicked() }
        viewBinding.detailsButton.setOnClickListener { onDetailsButtonClicked() }
    }

    private fun setupViewModel() {
        viewModel.getMediaItemViewState(args.index).observe(viewLifecycleOwner, ::onState)
    }

    private fun onState(state: MediaItemViewState) {
        this.state = state
        viewBinding.previousButton.isInvisible = state.previous == null
        viewBinding.nextButton.isInvisible = state.next == null
        viewBinding.imageView.contentDescription = state.current.fileName
        Glide.with(viewBinding.imageView)
            .load("${state.current.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
    }

    private fun onPreviousButtonClicked() {
        state?.previous?.let { item -> navigateToMediaItemView(item) }
    }

    private fun onNextButtonClicked() {
        state?.next?.let { item -> navigateToMediaItemView(item) }
    }

    private fun navigateToMediaItemView(item: MediaItemGridState.Item) {
        val directions = when (item.type) {
            is MediaItem.Type.Photo -> {
                MediaItemImageViewFragmentDirections.mediaItemImageView(item.index)
            }
            is MediaItem.Type.Video -> {
                MediaItemViewFragmentDirections.mediaItemDetails(item.index)
            }
        }
        findNavController().navigate(directions)
    }

    private fun onDetailsButtonClicked() {
        findNavController().navigate(
            MediaItemImageViewFragmentDirections.mediaItemDetails(args.index)
        )
    }
}
