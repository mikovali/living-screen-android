package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemGridFragment :
    VerticalGridSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val mainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)
    private val mediaItemsAdapter = ArrayObjectAdapter(MediaItemPresenter())

    init {
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = NUMBER_OF_COLUMNS
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mainFragmentAdapter
    }

    private fun setupViews() {
        adapter = mediaItemsAdapter
        setOnItemViewClickedListener { _, item, _, _ ->
            item as MediaItemGridState.Item
            findNavController().navigate(AlbumListFragmentDirections.mediaItemView(item.index))
        }
    }

    private fun setupViewModel() {
        viewModel.mediaItemGridState.observe(viewLifecycleOwner, ::onState)
    }

    private fun onState(state: MediaItemGridState) {
        mediaItemsAdapter.setItems(state.items, null)
    }
}

private const val NUMBER_OF_COLUMNS = 5
private const val THUMBNAIL_RATIO = 9f / 16f

private class MediaItemPresenter : Presenter() {

    private lateinit var mainImageSize: Point

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        if (!this::mainImageSize.isInitialized) {
            val grid = parent as VerticalGridView
            val width = ((grid.parent as View).width
                    - (grid.horizontalSpacing * (NUMBER_OF_COLUMNS - 1))
                    - grid.paddingStart
                    - grid.paddingEnd) / NUMBER_OF_COLUMNS
            mainImageSize = Point(width, (width * THUMBNAIL_RATIO).toInt())
        }

        return ViewHolder(ImageCardView(parent.context).apply {
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageAdjustViewBounds(false)
            setMainImageDimensions(mainImageSize.x, mainImageSize.y)
            setMainImageScaleType(ImageView.ScaleType.CENTER_CROP)
        })
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val mediaItem = item as MediaItemGridState.Item
        with((viewHolder.view as ImageCardView)) {
            Glide.with(this)
                .load("${mediaItem.baseUrl}=w${mainImageSize.x}-h${mainImageSize.y}-c")
                .placeholder(R.drawable.ic_baseline_photo_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(mainImageView)
            titleText = mediaItem.fileName
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        with((viewHolder.view as ImageCardView)) {
            Glide.with(this).clear(mainImageView)
        }
    }
}
