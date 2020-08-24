package com.sensorfields.livingscreen.android.album.list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemGridFragment :
    VerticalGridSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by viewModels<AlbumListViewModel>({ requireParentFragment() }) {
        producer { factory.get() }
    }

    private val mainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)

    private val mediaItemsAdapter = ArrayObjectAdapter(
        object : Presenter() {
            override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                return ViewHolder(ImageCardView(parent.context).apply {
                    isClickable = true
                    isFocusable = true
                    mainImage = resources.getDrawable(R.drawable.ic_launcher_foreground, null)
                })
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
                val mediaItem = item as MediaItem
                with(viewHolder.view as ImageCardView) {
                    Glide.with(this).load(mediaItem.thumbnail)
                        .into(object : CustomViewTarget<ImageCardView, Drawable>(this) {
                            override fun onLoadFailed(errorDrawable: Drawable?) {
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                view.mainImage = resource
                            }

                            override fun onResourceCleared(placeholder: Drawable?) {
                            }
                        })
                    titleText = mediaItem.fileName
                }
            }

            override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
        }
    )

    init {
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = NUMBER_OF_COLUMNS
        }
        adapter = mediaItemsAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeMediaItems(null).observe(viewLifecycleOwner) { mediaItems ->
            mediaItemsAdapter.setItems(mediaItems, null)
        }
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mainFragmentAdapter
    }
}

private const val NUMBER_OF_COLUMNS = 5
