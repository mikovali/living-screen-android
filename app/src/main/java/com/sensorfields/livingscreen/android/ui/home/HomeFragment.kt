package com.sensorfields.livingscreen.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.SectionRow
import androidx.lifecycle.lifecycleScope
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.ui.mediaitem.grid.MediaItemGridFragment
import com.sensorfields.livingscreen.android.ui.mediaitem.grid.MediaItemGridFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var albumsAdapter: ArrayObjectAdapter

    private fun onState(state: HomeState) {
        val items = buildList {
            add(PageRow(HeaderItem(getString(R.string.home_all))))

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
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageFragmentFactory())

        albumsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = albumsAdapter

        with(viewLifecycleOwner.lifecycleScope) {
            viewModel.state.onEach(::onState).launchIn(this)
        }
    }
}

private fun List<HomeState.AlbumItem>.toRows(): List<PageRow> = map { PageRow(it) }

private class PageFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {

    override fun createFragment(row: Any?): Fragment {
        if (row !is PageRow) throw IllegalArgumentException("row has to be instance of PageRow")

        val albumId = when (val item = row.headerItem) {
            is HomeState.AlbumItem -> item.id
            else -> null
        }

        return MediaItemGridFragment().apply {
            arguments = MediaItemGridFragmentArgs(albumId = albumId).toBundle()
        }
    }
}
