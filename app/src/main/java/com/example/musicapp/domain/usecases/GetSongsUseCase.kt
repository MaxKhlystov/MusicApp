package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository

class GetSongsUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): List<Song> {
        return repository.getAllSongs()
    }
}