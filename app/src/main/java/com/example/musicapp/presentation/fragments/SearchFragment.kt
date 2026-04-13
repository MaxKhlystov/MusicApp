package com.example.musicapp.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentSearchBinding
import com.example.musicapp.presentation.adapters.SearchResultAdapter
import com.example.musicapp.presentation.common.BaseFragment
import com.example.musicapp.presentation.viewmodels.SearchViewModel
import com.example.musicapp.utils.AppEvents
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var editTextSearch: AppCompatEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textEmptyState: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(rootView)

        setupMenu()
        initViews()
        setupSearchInput()
        setupRecyclerView()
        observeViewModel()
        observeAppEvents()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clear_cache -> {
                        clearCache()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun clearCache() {
        try {
            val cacheDir = File(requireContext().cacheDir, "okhttp_cache")
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
                showToast("Кэш успешно очищен")
            } else {
                showToast("Кэш пуст")
            }
        } catch (e: Exception) {
            showToast("Ошибка очистки кэша: ${e.message}")
        }
    }

    private fun initViews() {
        editTextSearch = binding.editTextSearch
        recyclerView = binding.recyclerViewSearchResults
        progressBar = binding.progressBar
        textEmptyState = binding.textEmptyState
    }

    private fun setupSearchInput() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter(
            onItemClick = { track ->
                showToast("${track.title} - ${track.artistName}")
            },
            onAddClick = { track ->
                viewModel.addToCollection(track)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { items ->
                adapter.updateItems(items)
                updateEmptyState(items.isEmpty())
            }.onFailure { error ->
                showToast("Ошибка: ${error.message}")
                updateEmptyState(true)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.addResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showToast("Песня добавлена в коллекцию")
                performSearch()
            }.onFailure { error ->
                showToast(error.message ?: "Ошибка")
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showToast(it)
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun observeAppEvents() {
        AppEvents.updateSearchResults.observe(viewLifecycleOwner) {
            performSearch()
        }
    }

    private fun performSearch() {
        val query = editTextSearch.text?.toString()?.trim() ?: ""
        viewModel.search(query)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        val currentText = editTextSearch.text?.toString()?.trim() ?: ""

        if (isEmpty && currentText.isNotBlank()) {
            textEmptyState.text = "Ничего не найдено"
            textEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else if (isEmpty && currentText.isBlank()) {
            textEmptyState.text = "Введите название или исполнителя для поиска"
            textEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}