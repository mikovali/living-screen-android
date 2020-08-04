package com.sensorfields.livingscreen.android.album.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.databinding.AlbumListFragmentBinding
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AlbumListFragment : Fragment(R.layout.album_list_fragment) {

    @Inject
    lateinit var factory: Provider<AlbumListViewModel>

    private val viewModel by viewModels<AlbumListViewModel> { producer { factory.get() } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(AlbumListFragmentBinding.bind(view)) {
            viewModel.state.observe(viewLifecycleOwner) { onState(it) }
        }
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun AlbumListFragmentBinding.onState(state: AlbumListState) {
        Log.e("AlbumListFragment", "STATE: $state")
    }

    private fun onAction(action: AlbumListAction) {
        when (action) {
            is AlbumListAction.NavigateToAccountCreate -> {
                findNavController().navigate(AlbumListFragmentDirections.accountCreate())
            }
        }
    }
}
