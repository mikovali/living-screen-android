package com.sensorfields.livingscreen.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SectionRow
import androidx.lifecycle.lifecycleScope
import com.sensorfields.livingscreen.android.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var albumsAdapter: ArrayObjectAdapter

    private fun onState(state: HomeState) {
        val items = buildList {
            add(ListRow(HeaderItem(getString(R.string.home_all)), ArrayObjectAdapter()))

            if (state.albums.isNotEmpty()) {
                add(DividerRow())
                add(SectionRow(getString(R.string.home_albums)))
                addAll(state.albums.toRows())
            }

            if (state.sharedAlbums.isNotEmpty()) {
                add(DividerRow())
                add(SectionRow(getString(R.string.home_shared_albums)))
                addAll(state.sharedAlbums.toRows())
            }
        }
        albumsAdapter.setItems(items, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = albumsAdapter

        with(viewLifecycleOwner.lifecycleScope) {
            viewModel.state.onEach(::onState).launchIn(this)
        }
    }
}

private fun List<HomeState.AlbumItem>.toRows(): List<ListRow> = map { album ->
    ListRow(HeaderItem(album.title), ArrayObjectAdapter())
}
