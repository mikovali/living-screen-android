package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.domain.MediaItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaItemDisplayFragment : DetailsSupportFragment() {

    private val args by navArgs<MediaItemDisplayFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val size =
            Point(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        adapter = ArrayObjectAdapter(FullWidthDetailsOverviewRowPresenter(Presenter())).apply {
            add(DetailsOverviewRow(args.mediaItem).apply {
                with(actionsAdapter as ArrayObjectAdapter) {
                    add(Action(1, "Do something yo"))
                    add(
                        Action(
                            1,
                            "Do something else yo",
                            "With yo this",
                            requireContext().getDrawable(R.drawable.ic_baseline_broken_image_24)
                        )
                    )
                    add(Action(1, "Do something yo completely different").apply {
                        icon = requireContext().getDrawable(R.drawable.ic_baseline_photo_24)
                    })
                }

                Glide.with(requireContext())
                    .load("${args.mediaItem.baseUrl}=w${size.x}-h${size.y}-c")
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            imageDrawable = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            })
        }
    }
}

private class Presenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val mediaItem = item as MediaItem
        vh.title.text = mediaItem.fileName
        vh.subtitle.text = "Subtitle yo yo"
        vh.body.text = "Text body yo\nYo YO Yo\nSsasdasd"
    }
}
