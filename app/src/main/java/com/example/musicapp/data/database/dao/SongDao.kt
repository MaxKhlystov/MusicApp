package com.example.musicapp.data.database.dao

import androidx.room.*
import com.example.musicapp.data.database.entities.SongEntity

@Dao
interface SongDao {

    @Query("""
        SELECT songs.*, COALESCE(artists.name, 'Неизвестно') AS artistName
        FROM songs
        LEFT JOIN artists ON songs.artistId = artists.id
        ORDER BY songs.title
    """)
    suspend fun getAllSongsWithArtist(): List<SongWithArtist>

    @Insert
    suspend fun insertSong(song: SongEntity)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}

data class SongWithArtist(
    val id: Long,
    val title: String,
    val duration: String,
    val genre: String,
    val artistId: Long,
    val artistName: String
)