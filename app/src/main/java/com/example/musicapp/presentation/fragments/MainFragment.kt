package com.example.musicapp.presentation.fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentMainBinding
import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.usecases.AddArtistUseCase
import com.example.musicapp.domain.usecases.AddSongUseCase
import com.example.musicapp.presentation.common.BaseFragment
import com.example.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private var artists: List<Artist> = emptyList()

    inner class MainFragmentListener {
        fun onAddArtistClick() {
            showAddArtistDialog()
        }

        fun onAddSongClick() {
            viewModel.addSong()
        }

        fun onClearClick() {
            viewModel.clearForm()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(rootView)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.listener = MainFragmentListener()

        setupDurationFilter()
        loadArtists()
        observeViewModel()
    }

    private fun setupDurationFilter() {
        binding.editTextDuration.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!source[i].isDigit() && source[i] != ':') return@InputFilter ""
            }
            null
        })
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { artistList ->
            artists = artistList
            updateArtistSpinner()
        }

        viewModel.addSongResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddSongUseCase.Result.Success -> {
                    showToast("Песня добавлена!")
                }
                else -> {}
            }
        }

        viewModel.addArtistResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddArtistUseCase.Result.Success -> {
                    showToast("Исполнитель добавлен")
                }
                else -> {}
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showToast(it) }
        }
    }

    private fun loadArtists() {
        viewModel.loadArtists()
    }

    private fun updateArtistSpinner() {
        val artistNames = artists.map { it.name }.ifEmpty { listOf("Неизвестно") }
        binding.artistNames = artistNames

        binding.spinnerArtist.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun showAddArtistDialog() {
        val input = android.widget.EditText(requireContext())

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Добавить исполнителя")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    showToast("Имя не может быть пустым")
                    return@setPositiveButton
                }
                viewModel.addArtist(name)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}