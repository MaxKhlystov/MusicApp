package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.repository.MusicRepository

class GetArtistsUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): List<Artist> {
        return repository.getAllArtists()
    }
}