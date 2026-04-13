package com.example.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.models.SearchResult
import com.example.musicapp.domain.models.Track
import com.example.musicapp.domain.usecases.AddToCollectionUseCase
import com.example.musicapp.domain.usecases.SearchSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
    private val addToCollectionUseCase: AddToCollectionUseCase
) : ViewModel() {

    private val _searchResults = MutableLiveData<Result<List<SearchResult>>>()
    val searchResults: LiveData<Result<List<SearchResult>>> = _searchResults

    private val _addResult = MutableLiveData<Result<Boolean>>()
    val addResult: LiveData<Result<Boolean>> = _addResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = Result.success(emptyList())
            return
        }

        _isLoading.value = true

        searchJob = viewModelScope.launch {
            delay(500)
            try {
                val result = searchSongsUseCase(query)
                _searchResults.value = when (result) {
                    is SearchSongsUseCase.Result.Success -> Result.success(result.items)
                    is SearchSongsUseCase.Result.Error -> Result.failure(Exception(result.message))
                    is SearchSongsUseCase.Result.EmptyQuery -> Result.success(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.value = Result.failure(e)
                _errorMessage.value = "Ошибка поиска: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCollection(track: Track) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = addToCollectionUseCase(track)
                when (result) {
                    is AddToCollectionUseCase.Result.Success -> {
                        _addResult.value = Result.success(true)
                        _errorMessage.value = "Песня добавлена в коллекцию"
                    }
                    is AddToCollectionUseCase.Result.AlreadyInCollection -> {
                        _addResult.value = Result.failure(Exception("Песня уже в коллекции"))
                        _errorMessage.value = "Песня уже в коллекции"
                    }
                    is AddToCollectionUseCase.Result.ArtistNotFound -> {
                        _addResult.value = Result.failure(Exception("Исполнитель не найден"))
                        _errorMessage.value = "Исполнитель не найден"
                    }
                    is AddToCollectionUseCase.Result.Error -> {
                        _addResult.value = Result.failure(Exception(result.message))
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _addResult.value = Result.failure(e)
                _errorMessage.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}