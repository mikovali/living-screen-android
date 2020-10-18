package com.sensorfields.livingscreen.android.mediaitem.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemListFragment : VerticalGridSupportFragment() {

    @Inject
    lateinit var factory: Provider<MediaItemListViewModel>

    private val viewModel by viewModels<MediaItemListViewModel> { producer { factory.get() } }

    private val mediaItemsAdapter = PagingDataAdapter(MediaItemPresenter(), DiffUtilCallback)

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

    private fun setupViews() {
        adapter = mediaItemsAdapter
        // TODO item click listener
//        setOnItemViewClickedListener { _, item, _, _ ->
//            item as MediaItemGridState.Item
//            findNavController().navigate(AlbumListFragmentDirections.mediaItemView(item.index))
//        }
    }

    private fun setupViewModel() {
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mediaItemsPagingData.collectLatest { mediaItemsAdapter.submitData(it) }
        }
    }

    private fun onAction(action: MediaItemListAction) {
        when (action) {
            MediaItemListAction.NavigateToAccountCreate -> {
                findNavController().navigate(MediaItemListFragmentDirections.accountCreate())
            }
        }
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
        val mediaItem = item as MediaItem
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

object DiffUtilCallback : DiffUtil.ItemCallback<MediaItem>() {

    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem == newItem
    }
}
