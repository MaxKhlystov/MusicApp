package com.example.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsListViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val deleteSongUseCase: DeleteSongUseCase,
    private val updateSongUseCase: UpdateSongUseCase,
    private val clearDatabaseUseCase: ClearDatabaseUseCase
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>(emptyList())
    val songs: LiveData<List<Song>> = _songs

    private val _deleteResult = MutableLiveData<Boolean>(false)
    val deleteResult: LiveData<Boolean> = _deleteResult

    private val _updateResult = MutableLiveData<Boolean>(false)
    val updateResult: LiveData<Boolean> = _updateResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadSongs() {
        viewModelScope.launch {
            try {
                val songs = getSongsUseCase()
                _songs.value = songs
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки песен: ${e.message}"
            }
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            try {
                deleteSongUseCase(song)
                _deleteResult.value = true
                loadSongs()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления: ${e.message}"
                _deleteResult.value = false
            }
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch {
            try {
                val result = updateSongUseCase(song)
                when (result) {
                    is UpdateSongUseCase.Result.Success -> {
                        _updateResult.value = true
                        loadSongs()
                    }
                    is UpdateSongUseCase.Result.EmptyTitle -> {
                        _errorMessage.value = "Название не может быть пустым"
                        _updateResult.value = false
                    }
                    is UpdateSongUseCase.Result.InvalidDuration -> {
                        _errorMessage.value = "Неверный формат длительности"
                        _updateResult.value = false
                    }
                    is UpdateSongUseCase.Result.SongNotFound -> {
                        _errorMessage.value = "Песня не найдена"
                        _updateResult.value = false
                    }
                    is UpdateSongUseCase.Result.Error -> {
                        _errorMessage.value = result.message
                        _updateResult.value = false
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления: ${e.message}"
                _updateResult.value = false
            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            try {
                clearDatabaseUseCase()
                loadSongs()
                _errorMessage.value = "База данных очищена"
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка очистки: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}