package com.example.musicapp.domain.usecases

import com.example.musicapp.domain.models.SearchResult
import com.example.musicapp.domain.models.Track
import com.example.musicapp.domain.repository.SearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchSongsUseCase(
    private val repository: SearchRepository,
    private val getSongsUseCase: GetSongsUseCase
) {
    sealed class Result {
        data class Success(val items: List<SearchResult>) : Result()
        data class Error(val message: String) : Result()
        object EmptyQuery : Result()
    }

    suspend operator fun invoke(query: String): Result {
        if (query.isBlank()) {
            return Result.EmptyQuery
        }

        val normalizedQuery = query.trim().lowercase()

        return try {
            val result = repository.searchSongs(normalizedQuery)

            result.fold(
                onSuccess = { searchResults ->
                    val existingSongs = getSongsUseCase()

                    val enrichedResults = searchResults.map { searchResult ->
                        val alreadyExists = existingSongs.any { song ->
                            song.title.equals(searchResult.track.title, ignoreCase = true) &&
                                    song.artistName.equals(searchResult.track.artistName, ignoreCase = true)
                        }
                        searchResult.copy(isAlreadyInCollection = alreadyExists)
                    }

                    Result.Success(enrichedResults)
                },
                onFailure = { error ->
                    Result.Error(error.message ?: "Неизвестная ошибка")
                }
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ошибка поиска")
        }
    }

    fun searchWithDebounce(query: String, debounceMs: Long = 500): Flow<Result> = flow {
        delay(debounceMs)
        emit(invoke(query))
    }
}