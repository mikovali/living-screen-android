package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.observe
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
                return ViewHolder(TextView(parent.context).apply {
                    isClickable = true
                    isFocusable = true
                })
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
                (viewHolder.view as TextView).text = (item as MediaItem).fileName
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
