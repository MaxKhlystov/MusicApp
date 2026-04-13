package com.example.musicapp.domain.models

data class Song(
    val id: Long = 0,
    val title: String,
    val duration: String,
    val genre: String,
    val artistId: Long,
    val artistName: String = ""
) {
    fun isValidDuration(): Boolean {
        return duration.matches(Regex("^\\d{1,2}:\\d{2}$"))
    }
}