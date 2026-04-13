package com.example.musicapp.data.repository

import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.database.entities.ArtistEntity
import com.example.musicapp.data.database.entities.SongEntity
import com.example.musicapp.data.network.api.AudioDbApiService
import com.example.musicapp.data.network.dto.TrackDto
import com.example.musicapp.domain.models.SearchResult
import com.example.musicapp.domain.models.Track
import com.example.musicapp.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val apiService: AudioDbApiService,
    private val database: AppDatabase
) : SearchRepository {

    private val songDao = database.songDao()
    private val artistDao = database.artistDao()
    private fun TrackDto.toDomain(): Track = Track(
        id = trackId,
        title = trackName,
        artistName = artistName,
        albumName = collectionName,
        durationMs = trackTimeMillis,
        genre = primaryGenreName,
        artworkUrl = artworkUrl100
    )

    override suspend fun searchSongs(query: String): Result<List<SearchResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchTracks(query)
                val tracks = response.results

                val existingSongs = songDao.getAllSongsWithArtist()

                val searchResults = tracks.map { trackDto ->
                    val track = trackDto.toDomain()

                    val alreadyExists = existingSongs.any {
                        it.title.equals(track.title, ignoreCase = true) &&
                                it.artistName.equals(track.artistName, ignoreCase = true)
                    }

                    SearchResult(
                        track = track,
                        isAlreadyInCollection = alreadyExists
                    )
                }

                Result.success(searchResults)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addToCollection(track: Track): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                var artist = artistDao.getArtistByName(track.artistName)
                if (artist == null) {
                    val artistId = artistDao.insertArtist(
                        ArtistEntity(name = track.artistName)
                    )
                    artist = ArtistEntity(id = artistId, name = track.artistName)
                }

                val duration = track.durationMs?.let { ms ->
                    val seconds = ms / 1000
                    String.format("%d:%02d", seconds / 60, seconds % 60)
                } ?: "3:30"

                val genre = track.genre ?: "Поп"

                val existingSongs = songDao.getAllSongsWithArtist()
                val alreadyExists = existingSongs.any {
                    it.title.equals(track.title, ignoreCase = true) &&
                            it.artistName.equals(track.artistName, ignoreCase = true)
                }

                if (alreadyExists) {
                    return@withContext Result.failure(Exception("Эта песня уже есть в вашей коллекции"))
                }

                val songEntity = SongEntity(
                    title = track.title,
                    duration = duration,
                    genre = genre,
                    artistId = artist?.id ?: 1
                )

                songDao.insertSong(songEntity)
                Result.success(true)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getTrackDetails(trackId: String): Result<Track?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTrackDetails(trackId)
                val trackDto = response.results.firstOrNull()
                Result.success(trackDto?.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}