package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoViewFragmentBinding
import com.sensorfields.livingscreen.android.producer
import com.sensorfields.livingscreen.android.viewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemPhotoViewFragment : Fragment(R.layout.media_item_photo_view_fragment) {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val viewBinding by viewState({ MediaItemPhotoViewFragmentBinding.bind(it) })

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
        onState(viewModel.getMediaItemViewState(args.index))
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
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemView(item.index))
    }

    private fun onDetailsButtonClicked() {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemDetails(args.index))
    }
}
