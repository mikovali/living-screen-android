package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemImageViewFragmentBinding

class MediaItemImageViewFragment : Fragment(R.layout.media_item_image_view_fragment) {

    private val args by navArgs<MediaItemImageViewFragmentArgs>()

    private val viewBinding by lazy { MediaItemImageViewFragmentBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val size = Point(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
        viewBinding.imageView.contentDescription = args.mediaItem.fileName
        Glide.with(viewBinding.imageView)
            .load("${args.mediaItem.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
    }
}
