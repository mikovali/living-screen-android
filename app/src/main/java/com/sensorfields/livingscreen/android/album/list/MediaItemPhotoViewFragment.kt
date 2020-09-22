package com.sensorfields.livingscreen.android.album.list

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.PlaybackControlsRow
import androidx.leanback.widget.PlaybackRowPresenter
import androidx.leanback.widget.RowPresenter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoBinding
import com.sensorfields.livingscreen.android.databinding.MediaItemPhotoControlsBinding
import com.sensorfields.livingscreen.android.producer
import com.sensorfields.livingscreen.android.viewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemPhotoViewFragment : PlaybackSupportFragment() {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val viewBinding by viewState(
        create = { view ->
            with(view as ViewGroup) {
                MediaItemPhotoBinding.inflate(LayoutInflater.from(context), this, false).apply {
                    this@with.addView(root, 0)
                }
            }
        }
    )

    private val size by lazy { with(resources.displayMetrics) { Point(widthPixels, heightPixels) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        hideControlsOverlay(false)
        setPlaybackRowPresenter(
            PhotoRowPresenter(
                skipPreviousActionClicked = ::navigateToMediaItemView,
                skipNextActionClicked = ::navigateToMediaItemView,
                moreActionsClicked = ::onDetailsClicked
            )
        )
    }

    private fun setupViewModel() {
        onState(viewModel.getMediaItemViewState(args.index))
    }

    private fun onState(state: MediaItemViewState) {
        viewBinding.imageView.contentDescription = state.current.fileName
        Glide.with(viewBinding.imageView)
            .load("${state.current.baseUrl}=w${size.x}-h${size.y}")
            .into(viewBinding.imageView)
        setPlaybackRow(PlaybackControlsRow(state))
    }

    private fun navigateToMediaItemView(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemView(item.index))
    }

    private fun onDetailsClicked(item: MediaItemGridState.Item) {
        findNavController().navigate(MediaItemViewFragmentDirections.mediaItemDetails(item.index))
    }
}

private class PhotoRowPresenter(
    private val skipPreviousActionClicked: (MediaItemGridState.Item) -> Unit,
    private val skipNextActionClicked: (MediaItemGridState.Item) -> Unit,
    private val moreActionsClicked: (MediaItemGridState.Item) -> Unit
) : PlaybackRowPresenter() {

    init {
        headerPresenter = null
        selectEffectEnabled = false
    }

    override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
        return ViewHolder(
            MediaItemPhotoControlsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(vh, item)
        val viewHolder = vh as ViewHolder
        val state = (viewHolder.row as PlaybackControlsRow).item as MediaItemViewState

        viewHolder.viewBinding.lbControlSkipPrevious.isVisible = state.previous != null
        viewHolder.viewBinding.lbControlSkipNext.isVisible = state.next != null

        viewHolder.viewBinding.lbControlSkipPrevious.setOnClickListener {
            state.previous?.let { skipPreviousActionClicked(it) }
        }
        viewHolder.viewBinding.lbControlSkipNext.setOnClickListener {
            state.next?.let { skipNextActionClicked(it) }
        }
        viewHolder.viewBinding.lbControlMoreActions.setOnClickListener {
            moreActionsClicked(state.current)
        }
    }

    class ViewHolder(
        val viewBinding: MediaItemPhotoControlsBinding
    ) : PlaybackRowPresenter.ViewHolder(viewBinding.root)
}
