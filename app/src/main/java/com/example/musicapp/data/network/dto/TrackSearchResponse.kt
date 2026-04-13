package com.example.musicapp.data.network.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)