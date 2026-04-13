package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.repository.MusicRepository

class AddArtistUseCase(
    private val repository: MusicRepository,
    private val getArtistsUseCase: GetArtistsUseCase
) {
    sealed class Result {
        data class Success(val artist: Artist) : Result()
        data class Error(val message: String) : Result()
        object EmptyName : Result()
        object AlreadyExists : Result()
    }

    suspend operator fun invoke(name: String): Result {
        if (name.isBlank()) {
            return Result.EmptyName
        }

        val normalizedName = name.trim()
            .split(" ")
            .joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.substring(0, 1).uppercase() + word.substring(1).lowercase()
                } else {
                    word
                }
            }

        val existingArtists = getArtistsUseCase()
        val exists = existingArtists.any { it.name.equals(normalizedName, ignoreCase = true) }

        if (exists) {
            return Result.AlreadyExists
        }

        val artist = repository.addArtistIfNotExists(normalizedName)

        return if (artist != null) {
            Result.Success(artist)
        } else {
            Result.Error("Не удалось создать исполнителя")
        }
    }
}