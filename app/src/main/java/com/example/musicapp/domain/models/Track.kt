package com.example.musicapp.domain.models

data class Track(
    val id: Long,
    val title: String,
    val artistName: String,
    val albumName: String? = null,
    val durationMs: Long? = null,
    val genre: String? = null,
    val artworkUrl: String? = null
) {
    fun getDurationFormatted(): String {
        return durationMs?.let { ms ->
            val seconds = ms / 1000
            String.format("%d:%02d", seconds / 60, seconds % 60)
        } ?: "--:--"
    }
}