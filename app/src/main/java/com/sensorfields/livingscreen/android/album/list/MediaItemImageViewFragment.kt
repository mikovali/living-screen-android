package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemImageViewFragmentBinding
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemImageViewFragment : Fragment(R.layout.media_item_image_view_fragment) {

    private val args by navArgs<MediaItemImageViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by viewModels<AlbumListViewModel>({ requireParentFragment() }) {
        producer { factory.get() }
    }

    private val viewBinding by lazy { MediaItemImageViewFragmentBinding.bind(requireView()) }

    private val size by lazy {
        Point(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.getMediaItemViewState(args.index).observe(viewLifecycleOwner, ::onState)
    }

    private fun onState(state: MediaItemViewState) {
        viewBinding.imageView.contentDescription = state.current.fileName
        Glide.with(viewBinding.imageView)
            .load("${state.current.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
    }
}
