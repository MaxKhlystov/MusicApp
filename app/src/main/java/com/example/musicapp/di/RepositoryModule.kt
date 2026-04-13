package com.example.musicapp.di

import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.repository.MusicRepositoryImpl
import com.example.musicapp.data.repository.SearchRepositoryImpl
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.SearchRepository
import com.example.musicapp.data.network.api.AudioDbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(database: AppDatabase): MusicRepository {
        return MusicRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        apiService: AudioDbApiService,
        database: AppDatabase
    ): SearchRepository {
        return SearchRepositoryImpl(apiService, database)
    }
}