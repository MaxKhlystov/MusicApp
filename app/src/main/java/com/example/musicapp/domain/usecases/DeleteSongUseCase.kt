package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository

class DeleteSongUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(song: Song) {
        repository.deleteSong(song)
    }
}