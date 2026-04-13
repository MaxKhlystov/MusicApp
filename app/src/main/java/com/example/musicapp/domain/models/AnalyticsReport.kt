package com.example.musicapp.domain.models

import java.text.SimpleDateFormat
import java.util.*

data class AnalyticsReport(
    val totalSongs: Int,
    val totalArtists: Int,
    val genreDistribution: Map<String, Int>,
    val mostPopularGenre: String,
    val timestamp: Long
) {
    fun format(): String {
        val genres = genreDistribution.entries
            .joinToString("\n") { "  ${it.key}: ${it.value} песен" }

        return """
            === Отчёт по коллекции ===
            
            Всего песен: $totalSongs
            Всего исполнителей: $totalArtists
            Самый популярный жанр: $mostPopularGenre
            
            Распределение по жанрам:
            $genres
            
            Создан: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date(timestamp))}
        """.trimIndent()
    }
}