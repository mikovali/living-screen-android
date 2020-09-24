package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.domain.MediaItem
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MediaItemViewFragment : Fragment(R.layout.media_item_view_fragment) {

    private val args by navArgs<MediaItemViewFragmentArgs>()

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by navGraphViewModels<AlbumListViewModel>(R.id.albumListFragment) {
        producer { factory.get() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        onState(viewModel.getMediaItemViewState(args.index))
    }

    private fun onState(state: MediaItemViewState) {
        val fragment = when (state.current.type) {
            MediaItem.Type.Photo -> MediaItemPhotoViewFragment()
            MediaItem.Type.Video -> MediaItemVideoViewFragment()
        }.apply {
            arguments = args.toBundle()
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.containerView, fragment)
            .commit()
    }
}
