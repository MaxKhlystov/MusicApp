package com.example.musicapp.data.database.dao

import androidx.room.*
import com.example.musicapp.data.database.entities.ArtistEntity

@Dao
interface ArtistDao {

    @Query("SELECT * FROM artists ORDER BY name")
    suspend fun getAllArtists(): List<ArtistEntity>

    @Query("SELECT * FROM artists WHERE id = :id LIMIT 1")
    suspend fun getArtistById(id: Long): ArtistEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtist(artist: ArtistEntity): Long

    @Query("SELECT * FROM artists WHERE name = :name LIMIT 1")
    suspend fun getArtistByName(name: String): ArtistEntity?

    @Query("DELETE FROM artists WHERE name != 'Неизвестно'")
    suspend fun deleteAllArtistsExceptUnknown()

    @Query("DELETE FROM artists")
    suspend fun deleteAllArtists()

    @Query("SELECT * FROM artists WHERE name = 'Неизвестно' LIMIT 1")
    suspend fun getUnknownArtist(): ArtistEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnknownArtist(artist: ArtistEntity)
}