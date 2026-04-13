package com.example.musicapp.di

import android.content.Context
import com.example.musicapp.data.network.NetworkModule
import com.example.musicapp.data.network.api.AudioDbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): AudioDbApiService {
        return NetworkModule.getApiService(context)
    }
}