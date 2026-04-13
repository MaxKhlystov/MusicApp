package com.example.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.models.AnalyticsReport
import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.usecases.GetAnalyticsUseCase
import com.example.musicapp.domain.usecases.GetArtistsUseCase
import com.example.musicapp.domain.usecases.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase,
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>(emptyList())
    val songs: LiveData<List<Song>> = _songs

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> = _artists

    private val _analyticsReport = MutableLiveData<AnalyticsReport>(
        AnalyticsReport(
            totalSongs = 0,
            totalArtists = 0,
            genreDistribution = emptyMap(),
            mostPopularGenre = "",
            timestamp = System.currentTimeMillis()
        )
    )
    val analyticsReport: LiveData<AnalyticsReport> = _analyticsReport

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

    fun loadArtists() {
        viewModelScope.launch {
            try {
                val artists = getArtistsUseCase()
                _artists.value = artists
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки артистов: ${e.message}"
            }
        }
    }

    fun getAnalytics() {
        viewModelScope.launch {
            try {
                val report = getAnalyticsUseCase()
                _analyticsReport.value = report
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка получения аналитики: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}