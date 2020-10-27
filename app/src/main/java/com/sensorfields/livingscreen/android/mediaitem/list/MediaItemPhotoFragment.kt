package com.sensorfields.livingscreen.android.mediaitem.list

import android.graphics.Point
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.PlaybackSupportFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoBinding
import com.sensorfields.livingscreen.android.producer
import javax.inject.Inject
import javax.inject.Provider

class MediaItemPhotoFragment : PlaybackSupportFragment() {

    private val args by navArgs<MediaItemPhotoFragmentArgs>()

    @Inject
    lateinit var factory: Provider<MediaItemListViewModel>

    private val viewModel by navGraphViewModels<MediaItemListViewModel>(
        navGraphId = R.id.mediaItemListFragment,
        factoryProducer = { producer { factory.get() } }
    )

    private lateinit var viewBinding: MediaItemPhotoBinding

    private val size by lazy { with(resources.displayMetrics) { Point(widthPixels, heightPixels) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = MediaItemPhotoBinding.inflate(
            LayoutInflater.from(view.context),
            view as ViewGroup,
            false
        ).apply { view.addView(root, 0) }

        hideControlsOverlay(false)
        setOnKeyInterceptListener { _, keyCode, event ->
            if (!isControlsOverlayVisible &&
                keyCode in arrayOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT)
            ) {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    viewModel.onPreviousClicked(args.mediaItem)
                } else if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                ) {
                    viewModel.onNextClicked(args.mediaItem)
                }
                true
            } else {
                false
            }
        }

        Glide.with(viewBinding.imageView)
            .load("${args.mediaItem.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)

        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun onAction(action: MediaItemListAction) {
        when (action) {
            is MediaItemListAction.NavigateToMediaItemPhoto -> {
                findNavController().navigate(
                    MediaItemPhotoFragmentDirections.mediaItemPhoto(action.mediaItem)
                )
            }
            is MediaItemListAction.NavigateToMediaItemVideo -> {
                findNavController().navigate(
                    MediaItemPhotoFragmentDirections.mediaItemVideo(action.mediaItem)
                )
            }
            else -> {
            }
        }
    }
}
