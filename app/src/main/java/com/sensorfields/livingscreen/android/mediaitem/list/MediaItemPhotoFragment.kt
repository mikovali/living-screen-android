package com.sensorfields.livingscreen.android.mediaitem.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoBinding

class MediaItemPhotoFragment : Fragment(R.layout.media_item_photo) {

    private val args by navArgs<MediaItemPhotoFragmentArgs>()

    private lateinit var viewBinding: MediaItemPhotoBinding

    private val size by lazy { with(resources.displayMetrics) { Point(widthPixels, heightPixels) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = MediaItemPhotoBinding.bind(view)

        Glide.with(viewBinding.imageView)
            .load("${args.mediaItem.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
    }
}
