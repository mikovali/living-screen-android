package com.sensorfields.livingscreen.android.mediaitem.list

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.AsyncPagingDataDiffer
import coil.clear
import coil.load
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.model.MediaItem
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemListFragment : VerticalGridSupportFragment() {

    @Inject
    lateinit var factory: Provider<MediaItemListViewModel>

    private val viewModel by navGraphViewModels<MediaItemListViewModel>(
        navGraphId = R.id.mediaItemListFragment,
        factoryProducer = { producer { factory.get() } }
    )

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
        adapter = MediaItemAdapter(
            MediaItemPresenter(),
            viewModel.differ,
            viewModel.listUpdateCallback,
            viewLifecycleOwner.lifecycleScope
        )
        setOnItemViewClickedListener { _, item, _, _ ->
            viewModel.onMediaItemClicked(item as MediaItem)
        }
    }

    private fun setupViewModel() {
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun onAction(action: MediaItemListAction) {
        when (action) {
            MediaItemListAction.NavigateToAccountCreate -> {
                findNavController().navigate(MediaItemListFragmentDirections.accountCreate())
            }
            is MediaItemListAction.NavigateToMediaItemPhoto -> {
                findNavController().navigate(
                    MediaItemListFragmentDirections.mediaItemPhoto(action.mediaItem)
                )
            }
            is MediaItemListAction.NavigateToMediaItemVideo -> {
                findNavController().navigate(
                    MediaItemListFragmentDirections.mediaItemVideo(action.mediaItem)
                )
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
            mainImageView.load("${mediaItem.baseUrl}=w${mainImageSize.x}-h${mainImageSize.y}-c") {
                placeholder(R.drawable.ic_baseline_photo_24)
                error(R.drawable.ic_baseline_broken_image_24)
            }
            titleText = mediaItem.fileName
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        with((viewHolder.view as ImageCardView)) {
            mainImageView.clear()
        }
    }
}

private class MediaItemAdapter(
    presenter: Presenter,
    private val differ: AsyncPagingDataDiffer<MediaItem>,
    listUpdateCallback: FlowListUpdateCallback,
    scope: CoroutineScope
) : ObjectAdapter(presenter) {

    init {
        listUpdateCallback.events
            .onEach { event ->
                when (event) {
                    is FlowListUpdateCallback.Event.OnInserted -> {
                        notifyItemRangeInserted(event.position, event.count)
                    }
                    is FlowListUpdateCallback.Event.OnRemoved -> {
                        notifyItemRangeRemoved(event.position, event.count)
                    }
                    is FlowListUpdateCallback.Event.OnMoved -> {
                        notifyItemMoved(event.fromPosition, event.toPosition)
                    }
                    is FlowListUpdateCallback.Event.OnChanged -> {
                        notifyItemRangeChanged(event.position, event.count, event.payload)
                    }
                }
            }
            .launchIn(scope)
    }

    override fun size(): Int = differ.itemCount

    override fun get(position: Int): MediaItem? = differ.getItem(position)
}
