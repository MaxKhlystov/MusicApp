package com.example.musicapp.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.musicapp.domain.models.Artist

@Entity(
    tableName = "artists",
    indices = [Index(value = ["name"], unique = true)]
)
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
) {
    fun toDomain(): Artist = Artist(id = id, name = name)

    companion object {
        fun fromDomain(artist: Artist): ArtistEntity = ArtistEntity(
            id = artist.id,
            name = artist.name
        )
    }
}