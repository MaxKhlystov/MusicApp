package com.example.musicapp.domain.repository

import com.example.musicapp.domain.models.Track
import com.example.musicapp.domain.models.SearchResult

interface SearchRepository {
    suspend fun searchSongs(query: String): Result<List<SearchResult>>
    suspend fun addToCollection(track: Track): Result<Boolean>
    suspend fun getTrackDetails(trackId: String): Result<Track?>
}