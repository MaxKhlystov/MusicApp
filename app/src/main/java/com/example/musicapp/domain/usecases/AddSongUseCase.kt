package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository

class AddSongUseCase(
    private val repository: MusicRepository,
    private val getArtistsUseCase: GetArtistsUseCase
) {
    sealed class Result {
        data class Success(val song: Song) : Result()
        data class Error(val message: String) : Result()
        object EmptyTitle : Result()
        object InvalidDuration : Result()
        object ArtistNotFound : Result()
        object SongAlreadyExists : Result()
    }

    suspend operator fun invoke(
        title: String,
        duration: String,
        genre: String,
        artistName: String
    ): Result {
        if (title.isBlank()) {
            return Result.EmptyTitle
        }

        if (!duration.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
            return Result.InvalidDuration
        }

        val artists = getArtistsUseCase()
        var artist = artists.find { it.name.equals(artistName, ignoreCase = true) }

        if (artist == null) {
            artist = repository.addArtistIfNotExists(artistName)
            if (artist == null) {
                return Result.ArtistNotFound
            }
        }

        val existingSongs = repository.getAllSongs()
        val alreadyExists = existingSongs.any {
            it.title.equals(title, ignoreCase = true) && it.artistId == artist.id
        }

        if (alreadyExists) {
            return Result.SongAlreadyExists
        }

        val song = Song(
            title = title,
            duration = duration,
            genre = genre,
            artistId = artist.id,
            artistName = artist.name
        )

        repository.addSong(song)

        return Result.Success(song)
    }
}