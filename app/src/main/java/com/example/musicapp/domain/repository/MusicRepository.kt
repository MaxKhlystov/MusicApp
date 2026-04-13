package com.example.musicapp.domain.repository

import com.example.musicapp.domain.models.Artist
import com.example.musicapp.domain.models.Song

interface MusicRepository {
    suspend fun getAllSongs(): List<Song>
    suspend fun getAllArtists(): List<Artist>
    suspend fun addSong(song: Song)
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(song: Song)
    suspend fun addArtistIfNotExists(name: String): Artist?
    suspend fun clearSongs()
    suspend fun clearArtists(keepUnknown: Boolean)
}