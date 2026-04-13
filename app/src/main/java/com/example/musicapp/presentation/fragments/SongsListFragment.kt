package com.example.musicapp.presentation.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentSongsListBinding
import com.example.musicapp.domain.models.Song
import com.example.musicapp.presentation.adapters.SongAdapter
import com.example.musicapp.presentation.callbacks.SimpleItemTouchHelperCallback
import com.example.musicapp.presentation.common.BaseFragment
import com.example.musicapp.presentation.dialogs.EditSongDialog
import com.example.musicapp.presentation.viewmodels.SongsListViewModel
import com.example.musicapp.utils.AppEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongsListFragment : BaseFragment(R.layout.fragment_songs_list) {

    private val viewModel: SongsListViewModel by viewModels()
    private lateinit var binding: FragmentSongsListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSongsListBinding.bind(rootView)

        setupMenu()
        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()
        loadSongs()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.songs_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_database_clean -> {
                        showClearDbDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        recyclerView = binding.recyclerViewSongs
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = SongAdapter(
            emptyList(),
            onItemLongClick = { song -> showEditDialog(song) }
        )
        recyclerView.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val callback = SimpleItemTouchHelperCallback { position ->
            adapter.getSongAtPosition(position)?.let { song ->
                deleteSong(song)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun observeViewModel() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("Песня удалена")
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("Песня обновлена")
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showToast(it)
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun loadSongs() {
        viewModel.loadSongs()
    }

    private fun showEditDialog(song: Song) {
        EditSongDialog(
            activity = requireActivity(),
            viewModel = viewModel,
            song = song,
            onSaved = { loadSongs() }
        ).show()
    }

    private fun deleteSong(song: Song) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Удалить песню?")
            .setMessage("'${song.title}' - ${song.artistName}")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteSong(song)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showClearDbDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Полная очистка базы данных")
            .setMessage("Вы уверены, что хотите полностью очистить базу данных?\n\n" +
                    "• Будут удалены ВСЕ песни\n" +
                    "• Будут удалены ВСЕ артисты (кроме \"Неизвестно\")\n" +
                    "• Действие нельзя отменить!")
            .setPositiveButton("Очистить всё") { _, _ ->
                lifecycleScope.launch {
                    viewModel.clearDatabase()
                    delay(100)
                    loadSongs()
                    AppEvents.notifyUpdateSearchResults()
                    showToast("База данных полностью очищена")
                }
            }
            .setNegativeButton("Отмена", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}