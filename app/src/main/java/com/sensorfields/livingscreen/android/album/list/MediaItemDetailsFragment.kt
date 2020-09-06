package com.sensorfields.livingscreen.android.album.list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemDetailsFragment : DetailsSupportFragment() {

    private val args by navArgs<MediaItemDetailsFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val detailsAdapter = ArrayObjectAdapter(
        FullWidthDetailsOverviewRowPresenter(Presenter(), LogoPresenter())
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        adapter = detailsAdapter
    }

    private fun setupViewModel() {
        viewModel.getMediaItemViewState(args.index).observe(viewLifecycleOwner, ::onState)
    }

    private fun onState(state: MediaItemViewState) {
        detailsAdapter.setItems(
            listOf(
                DetailsOverviewRow(state.current).apply {
                    with(actionsAdapter as ArrayObjectAdapter) {
                        add(Action(ID1, "Do something yo"))
                        add(
                            Action(
                                ID2,
                                "Do something else yo",
                                "With yo this",
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_baseline_broken_image_24
                                )
                            )
                        )
                        add(Action(ID3, "Do something yo completely different").apply {
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_baseline_photo_24
                            )
                        })
                    }
                }
            ),
            null
        )
    }
}

private class Presenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val mediaItem = item as MediaItemGridState.Item
        vh.title.text = mediaItem.fileName
        vh.subtitle.text = "Subtitle yo yo"
        vh.body.text = "Text body yo\nYo YO Yo\nSsasdasd"
    }
}

private class LogoPresenter : DetailsOverviewLogoPresenter() {

    override fun onBindViewHolder(
        viewHolder: androidx.leanback.widget.Presenter.ViewHolder,
        item: Any
    ) {
        val mediaItem = (item as DetailsOverviewRow).item as MediaItemGridState.Item
        val vh = viewHolder as ViewHolder
        val imageView = vh.view as ImageView
        Glide.with(imageView)
            .load("${mediaItem.baseUrl}=w${imageView.maxWidth}-h${imageView.maxHeight}")
            .into(object : DrawableImageViewTarget(imageView) {
                override fun setResource(resource: Drawable?) {
                    super.setResource(resource)
                    if (resource == null) return
                    view.viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                view.layoutParams = view.layoutParams.apply {
                                    width = view.width
                                    height = view.height
                                }
                                vh.parentPresenter.notifyOnBindLogo(vh.parentViewHolder)
                                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }
                    )
                }
            })
    }

    override fun onUnbindViewHolder(viewHolder: androidx.leanback.widget.Presenter.ViewHolder) {
        Glide.with(viewHolder.view).clear(viewHolder.view)
    }

    override fun isBoundToImage(viewHolder: ViewHolder, row: DetailsOverviewRow?): Boolean {
        return (viewHolder.view as ImageView).drawable != null
    }
}

private const val ID1 = 1L
private const val ID2 = 2L
private const val ID3 = 3L
