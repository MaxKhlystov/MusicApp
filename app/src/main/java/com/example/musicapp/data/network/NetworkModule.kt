package com.example.musicapp.data.network

import android.content.Context
import com.example.musicapp.data.network.api.AudioDbApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "https://itunes.apple.com/"
    private const val CACHE_SIZE = 50L * 1024 * 1024 // 50 MB

    fun provideOkHttpClient(cacheDir: File): OkHttpClient {
        val cache = Cache(File(cacheDir, "okhttp_cache"), CACHE_SIZE)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                if (request.method == "GET") {
                    response.newBuilder()
                        .header("Cache-Control", "public, max-age=300")
                        .build()
                } else {
                    response
                }
            }
            .build()
    }

    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getApiService(context: Context): AudioDbApiService {
        val cacheDir = context.cacheDir
        val okHttpClient = provideOkHttpClient(cacheDir)
        val gson = provideGson()
        val retrofit = provideRetrofit(okHttpClient, gson)
        return retrofit.create(AudioDbApiService::class.java)
    }
}