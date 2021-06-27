package com.sensorfields.livingscreen.android.ui.mediaitem.grid

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import coil.clear
import coil.load
import com.sensorfields.livingscreen.android.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaItemGridFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider {

    private val viewModel by viewModels<MediaItemGridViewModel>()

    private val browseAdapter = MainFragmentAdapter(this)
    private val itemAdapter = PagingDataAdapter(
        presenter = ItemPresenter(),
        diffCallback = DiffUtilCallback
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = NUMBER_OF_COLUMNS
        }
        adapter = itemAdapter
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewLifecycleOwner.lifecycleScope) {
            launch { viewModel.items.collectLatest { itemAdapter.submitData(it) } }
        }
    }

    override fun getMainFragmentAdapter():
            BrowseSupportFragment.MainFragmentAdapter<MediaItemGridFragment> {
        return browseAdapter
    }

    private class MainFragmentAdapter(
        fragment: MediaItemGridFragment
    ) : BrowseSupportFragment.MainFragmentAdapter<MediaItemGridFragment>(fragment)
}

private class ItemPresenter : Presenter() {

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
        val mediaItem = item as MediaItemGridState.MediaItemItem
        with((viewHolder.view as ImageCardView)) {
            mainImageView.load("${mediaItem.baseUrl}=w${mainImageSize.x}-h${mainImageSize.y}-c") {
                placeholder(R.drawable.ic_baseline_photo_24)
                error(R.drawable.ic_baseline_broken_image_24)
            }
            titleText = mediaItem.filename
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        with((viewHolder.view as ImageCardView)) {
            mainImageView.clear()
        }
    }
}

private object DiffUtilCallback : DiffUtil.ItemCallback<MediaItemGridState.MediaItemItem>() {

    override fun areItemsTheSame(
        oldItem: MediaItemGridState.MediaItemItem,
        newItem: MediaItemGridState.MediaItemItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MediaItemGridState.MediaItemItem,
        newItem: MediaItemGridState.MediaItemItem
    ): Boolean {
        return oldItem == newItem
    }
}

private const val NUMBER_OF_COLUMNS = 5
private const val THUMBNAIL_RATIO = 9f / 16f
