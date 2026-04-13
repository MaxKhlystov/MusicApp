package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository

class UpdateSongUseCase(
    private val repository: MusicRepository
) {
    sealed class Result {
        data class Success(val song: Song) : Result()
        data class Error(val message: String) : Result()
        object EmptyTitle : Result()
        object InvalidDuration : Result()
        object SongNotFound : Result()
    }

    suspend operator fun invoke(song: Song): Result {
        if (song.title.isBlank()) {
            return Result.EmptyTitle
        }

        if (!song.duration.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
            return Result.InvalidDuration
        }

        val existingSongs = repository.getAllSongs()
        val exists = existingSongs.any { it.id == song.id }

        if (!exists) {
            return Result.SongNotFound
        }

        repository.updateSong(song)

        return Result.Success(song)
    }
}