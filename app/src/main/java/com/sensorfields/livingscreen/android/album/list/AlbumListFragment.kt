package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.SectionRow
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.mediaitem.list.MediaItemListFragment
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AlbumListFragment : BrowseSupportFragment() {

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val mediaItemGridFragmentFactory = object : FragmentFactory<MediaItemListFragment>() {
        private val mediaItemGridFragment = MediaItemListFragment()
        override fun createFragment(row: Any?): MediaItemListFragment {
            return mediaItemGridFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewModel.state.observe(viewLifecycleOwner, ::onState)
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun setupViews() {
        title = getString(R.string.album_list_title)
        adapter = rowsAdapter
        mainFragmentRegistry.registerFragment(PageRow::class.java, mediaItemGridFragmentFactory)
    }

    private fun onState(state: AlbumListState) {
        rowsAdapter.setItems(
            listOf(
                PageRow(HeaderItem(getString(R.string.album_list_all))),
                DividerRow(),
                SectionRow(getString(R.string.album_list_albums))
            ) + state.albums.map { album ->
                PageRow(HeaderItem(formatAlbumTitle(album.title)))
            } + listOf(
                DividerRow(),
                SectionRow(getString(R.string.album_list_shared_albums))
            ) + state.sharedAlbums.map { album ->
                PageRow(HeaderItem(formatAlbumTitle(album.title)))
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
