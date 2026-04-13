package com.example.musicapp.data.repository

import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.database.dao.SongWithArtist
import com.example.musicapp.data.database.entities.ArtistEntity
import com.example.musicapp.data.database.entities.SongEntity
import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepositoryImpl(
    private val database: AppDatabase
) : MusicRepository {

    private val songDao = database.songDao()
    private val artistDao = database.artistDao()

    override suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songsWithArtist = songDao.getAllSongsWithArtist()
        songsWithArtist.map { song ->
            Song(
                id = song.id,
                title = song.title,
                duration = song.duration,
                genre = song.genre,
                artistId = song.artistId,
                artistName = song.artistName
            )
        }
    }

    override suspend fun getAllArtists(): List<Artist> = withContext(Dispatchers.IO) {
        artistDao.getAllArtists().map { Artist(it.id, it.name) }
    }

    override suspend fun addSong(song: Song) = withContext(Dispatchers.IO) {
        songDao.insertSong(SongEntity.fromDomain(song))
    }

    override suspend fun updateSong(song: Song) = withContext(Dispatchers.IO) {
        songDao.updateSong(SongEntity.fromDomain(song))
    }

    override suspend fun deleteSong(song: Song) = withContext(Dispatchers.IO) {
        songDao.deleteSong(SongEntity.fromDomain(song))
    }

    override suspend fun addArtistIfNotExists(name: String): Artist? = withContext(Dispatchers.IO) {
        val existing = artistDao.getArtistByName(name)
        if (existing != null) {
            Artist(existing.id, existing.name)
        } else {
            val id = artistDao.insertArtist(ArtistEntity(name = name))
            if (id > 0) Artist(id, name) else null
        }
    }

    override suspend fun clearSongs() = withContext(Dispatchers.IO) {
        songDao.deleteAllSongs()
    }

    override suspend fun clearArtists(keepUnknown: Boolean) = withContext(Dispatchers.IO) {
        if (keepUnknown) {
            artistDao.deleteAllArtistsExceptUnknown()
        } else {
            artistDao.deleteAllArtists()
        }
        val unknown = artistDao.getUnknownArtist()
        if (unknown == null) {
            artistDao.insertUnknownArtist(ArtistEntity(name = "Неизвестно"))
        }
    }
}