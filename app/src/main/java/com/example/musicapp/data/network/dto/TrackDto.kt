package com.example.musicapp.data.network.dto

data class TrackDto(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String? = null,
    val trackTimeMillis: Long? = null,
    val primaryGenreName: String? = null,
    val artworkUrl100: String? = null,
    val previewUrl: String? = null,
    val country: String? = null,
    val currency: String,
    val trackPrice: Double? = null
) {
    fun getDurationFormatted(): String {
        return trackTimeMillis?.div(1000)?.toInt()?.let { seconds ->
            String.format("%d:%02d", seconds / 60, seconds % 60)
        } ?: "--:--"
    }

    val idTrack: String get() = trackId.toString()
    val strTrack: String get() = trackName
    val strArtist: String get() = artistName
    val strAlbum: String? get() = collectionName
    val intDuration: String? get() = trackTimeMillis?.div(1000)?.toString()
    val strGenre: String? get() = primaryGenreName
    val strTrackThumb: String? get() = artworkUrl100?.replace("100x100", "300x300")
}