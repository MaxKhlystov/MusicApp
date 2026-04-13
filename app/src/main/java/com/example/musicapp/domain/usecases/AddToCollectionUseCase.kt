package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.Track
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.SearchRepository

class AddToCollectionUseCase(
    private val musicRepository: MusicRepository,
    private val searchRepository: SearchRepository,
    private val getSongsUseCase: GetSongsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase
) {
    sealed class Result {
        data class Success(val song: Song) : Result()
        data class Error(val message: String) : Result()
        object AlreadyInCollection : Result()
        object ArtistNotFound : Result()
    }

    suspend operator fun invoke(track: Track): Result {
        val existingSongs = getSongsUseCase()
        val alreadyExists = existingSongs.any {
            it.title.equals(track.title, ignoreCase = true) &&
                    it.artistName.equals(track.artistName, ignoreCase = true)
        }

        if (alreadyExists) {
            return Result.AlreadyInCollection
        }

        val artists = getArtistsUseCase()
        var artist = artists.find { it.name.equals(track.artistName, ignoreCase = true) }

        if (artist == null) {
            artist = musicRepository.addArtistIfNotExists(track.artistName)
            if (artist == null) {
                return Result.ArtistNotFound
            }
        }

        val duration = track.durationMs?.let { ms ->
            val seconds = ms / 1000
            String.format("%d:%02d", seconds / 60, seconds % 60)
        } ?: "3:30"

        val genre = track.genre ?: "Поп"

        val song = Song(
            title = track.title,
            duration = duration,
            genre = genre,
            artistId = artist.id,
            artistName = artist.name
        )

        musicRepository.addSong(song)

        return Result.Success(song)
    }
}