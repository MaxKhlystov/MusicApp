package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.repository.MusicRepository

class ClearDatabaseUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke() {
        repository.clearSongs()
        repository.clearArtists(keepUnknown = true)
    }
}