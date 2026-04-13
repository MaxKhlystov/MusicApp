package com.example.musicapp.domain.models

data class SearchResult(
    val track: Track,
    val isAlreadyInCollection: Boolean = false
)