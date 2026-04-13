package com.example.musicapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.musicapp.domain.models.Song

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index("artistId")]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val duration: String,
    val genre: String,
    val artistId: Long = 1
) {
    fun toDomain(artistName: String = ""): Song = Song(
        id = id,
        title = title,
        duration = duration,
        genre = genre,
        artistId = artistId,
        artistName = artistName
    )

    companion object {
        fun fromDomain(song: Song): SongEntity = SongEntity(
            id = song.id,
            title = song.title,
            duration = song.duration,
            genre = song.genre,
            artistId = song.artistId
        )
    }
}