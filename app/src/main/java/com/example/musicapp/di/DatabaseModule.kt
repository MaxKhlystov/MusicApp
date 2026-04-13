package com.example.musicapp.di

import android.content.Context
import androidx.room.Room
import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.database.dao.ArtistDao
import com.example.musicapp.data.database.dao.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    @Provides
    fun provideSongDao(database: AppDatabase): SongDao = database.songDao()
    @Provides
    fun provideArtistDao(database: AppDatabase): ArtistDao = database.artistDao()
}