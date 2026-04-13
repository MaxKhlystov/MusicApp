package com.example.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.databinding.ObservableField
import androidx.databinding.ObservableBoolean
import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.usecases.AddArtistUseCase
import com.example.musicapp.domain.usecases.AddSongUseCase
import com.example.musicapp.domain.usecases.GetArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArtistsUseCase: GetArtistsUseCase,
    private val addArtistUseCase: AddArtistUseCase,
    private val addSongUseCase: AddSongUseCase
) : ViewModel() {
    val songTitle = ObservableField<String>("")
    val songDuration = ObservableField<String>("")
    val selectedGenre = ObservableField<String>("Поп")

    val isAddButtonEnabled = ObservableBoolean(false)

    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> = _artists

    private val _addSongResult = MutableLiveData<AddSongUseCase.Result>()
    val addSongResult: LiveData<AddSongUseCase.Result> = _addSongResult

    private val _addArtistResult = MutableLiveData<AddArtistUseCase.Result>()
    val addArtistResult: LiveData<AddArtistUseCase.Result> = _addArtistResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        songTitle.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable, propertyId: Int) {
                updateAddButtonState()
            }
        })

        songDuration.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable, propertyId: Int) {
                updateAddButtonState()
            }
        })
    }

    private fun updateAddButtonState() {
        val title = songTitle.get() ?: ""
        val duration = songDuration.get() ?: ""
        isAddButtonEnabled.set(title.isNotBlank() && duration.isNotBlank())
    }

    fun setSelectedGenre(genre: String) {
        selectedGenre.set(genre)
    }

    fun loadArtists() {
        viewModelScope.launch {
            try {
                _artists.value = getArtistsUseCase()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки артистов: ${e.message}"
            }
        }
    }

    fun addArtist(name: String) {
        viewModelScope.launch {
            val result = addArtistUseCase(name)
            _addArtistResult.value = result
            if (result is AddArtistUseCase.Result.Success) {
                loadArtists()
            } else {
                _errorMessage.value = when (result) {
                    is AddArtistUseCase.Result.EmptyName -> "Имя не может быть пустым"
                    is AddArtistUseCase.Result.AlreadyExists -> "Такой исполнитель уже есть"
                    is AddArtistUseCase.Result.Error -> result.message
                    else -> null
                }
            }
        }
    }

    fun addSong() {
        val title = songTitle.get() ?: ""
        val duration = songDuration.get() ?: ""
        val artistName = getSelectedArtistName() ?: ""
        val genre = selectedGenre.get() ?: "Поп"

        viewModelScope.launch {
            val result = addSongUseCase(title, duration, genre, artistName)
            _addSongResult.value = result

            when (result) {
                is AddSongUseCase.Result.Success -> {
                    clearForm()
                }
                else -> {
                    _errorMessage.value = when (result) {
                        is AddSongUseCase.Result.EmptyTitle -> "Название не может быть пустым"
                        is AddSongUseCase.Result.InvalidDuration -> "Неверный формат длительности (MM:SS)"
                        is AddSongUseCase.Result.ArtistNotFound -> "Исполнитель не найден"
                        is AddSongUseCase.Result.SongAlreadyExists -> "Эта песня уже есть в коллекции"
                        is AddSongUseCase.Result.Error -> result.message
                        else -> null
                    }
                }
            }
        }
    }

    fun clearForm() {
        songTitle.set("")
        songDuration.set("")
        selectedGenre.set("Поп")
    }

    fun getSelectedArtistName(): String? {
        return null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}