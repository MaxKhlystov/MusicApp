package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.AnalyticsReport
import com.example.musicapp.domain.models.Song
import com.example.musicapp.domain.models.Artist

class GetAnalyticsUseCase(
    private val getSongsUseCase: GetSongsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase
) {
    suspend operator fun invoke(): AnalyticsReport {
        val songs = getSongsUseCase()
        val artists = getArtistsUseCase()

        val genreStats = songs.groupBy { it.genre }
            .mapValues { (_, songsList) -> songsList.size }

        return AnalyticsReport(
            totalSongs = songs.size,
            totalArtists = artists.size,
            genreDistribution = genreStats,
            mostPopularGenre = genreStats.maxByOrNull { it.value }?.key ?: "Нет",
            timestamp = System.currentTimeMillis()
        )
    }
}