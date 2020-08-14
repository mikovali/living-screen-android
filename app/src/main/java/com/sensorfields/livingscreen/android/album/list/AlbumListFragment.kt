package com.sensorfields.livingscreen.android.album.list

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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

// TODO rename to AlbumBrowseFragment?
@AndroidEntryPoint
class AlbumListFragment : BrowseSupportFragment() {

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by viewModels<AlbumListViewModel> { producer { factory.get() } }

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewModel.state.observe(viewLifecycleOwner, ::onState)
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun setupViews() {
        title = getString(R.string.album_list_title)
        adapter = rowsAdapter
    }

    private fun onState(state: AlbumListState) {
        rowsAdapter.setItems(
            listOf(
                ListRow(HeaderItem(getString(R.string.album_list_all)), ArrayObjectAdapter()),
                DividerRow(),
                SectionRow(getString(R.string.album_list_albums))
            ) + state.albums.map { album ->
                ListRow(HeaderItem(formatAlbumTitle(album.title)), ArrayObjectAdapter())
            } + listOf(
                DividerRow(),
                SectionRow(getString(R.string.album_list_shared_albums))
            ) + state.sharedAlbums.map { album ->
                ListRow(HeaderItem(formatAlbumTitle(album.title)), ArrayObjectAdapter())
            },
            null
        )
    }

    private fun onAction(action: AlbumListAction) {
        when (action) {
            is AlbumListAction.NavigateToAccountCreate -> {
                findNavController().navigate(AlbumListFragmentDirections.accountCreate())
            }
        }
    }

    private fun formatAlbumTitle(title: String): String {
        return if (title.isBlank()) getString(R.string.album_list_album_title_blank) else title
    }
}
