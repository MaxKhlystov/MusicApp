package com.example.musicapp.data.network.api

import com.example.musicapp.data.network.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AudioDbApiService {

    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("country") country: String = "RU",
        @Query("limit") limit: Int = 100
    ): TrackSearchResponse

    @GET("lookup")
    suspend fun getTrackDetails(
        @Query("id") trackId: String,
        @Query("country") country: String = "RU"
    ): TrackSearchResponse
}