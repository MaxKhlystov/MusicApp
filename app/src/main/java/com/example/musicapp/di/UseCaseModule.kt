package com.example.musicapp.di

import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.SearchRepository
import com.example.musicapp.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetSongsUseCase(repo: MusicRepository): GetSongsUseCase {
        return GetSongsUseCase(repo)
    }

    @Provides
    fun provideGetArtistsUseCase(repo: MusicRepository): GetArtistsUseCase {
        return GetArtistsUseCase(repo)
    }

    @Provides
    fun provideAddSongUseCase(
        repo: MusicRepository,
        getArtistsUseCase: GetArtistsUseCase
    ): AddSongUseCase {
        return AddSongUseCase(repo, getArtistsUseCase)
    }

    @Provides
    fun provideUpdateSongUseCase(repo: MusicRepository): UpdateSongUseCase {
        return UpdateSongUseCase(repo)
    }

    @Provides
    fun provideDeleteSongUseCase(repo: MusicRepository): DeleteSongUseCase {
        return DeleteSongUseCase(repo)
    }

    @Provides
    fun provideAddArtistUseCase(
        repo: MusicRepository,
        getArtistsUseCase: GetArtistsUseCase
    ): AddArtistUseCase {
        return AddArtistUseCase(repo, getArtistsUseCase)
    }

    @Provides
    fun provideClearDatabaseUseCase(repo: MusicRepository): ClearDatabaseUseCase {
        return ClearDatabaseUseCase(repo)
    }

    @Provides
    fun provideSearchSongsUseCase(
        searchRepo: SearchRepository,
        getSongsUseCase: GetSongsUseCase
    ): SearchSongsUseCase {
        return SearchSongsUseCase(searchRepo, getSongsUseCase)
    }

    @Provides
    fun provideAddToCollectionUseCase(
        musicRepo: MusicRepository,
        searchRepo: SearchRepository,
        getSongsUseCase: GetSongsUseCase,
        getArtistsUseCase: GetArtistsUseCase
    ): AddToCollectionUseCase {
        return AddToCollectionUseCase(musicRepo, searchRepo, getSongsUseCase, getArtistsUseCase)
    }

    @Provides
    fun provideGetAnalyticsUseCase(
        getSongsUseCase: GetSongsUseCase,
        getArtistsUseCase: GetArtistsUseCase
    ): GetAnalyticsUseCase {
        return GetAnalyticsUseCase(getSongsUseCase, getArtistsUseCase)
    }
}