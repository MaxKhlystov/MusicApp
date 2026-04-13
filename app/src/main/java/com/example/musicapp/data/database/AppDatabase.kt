package com.example.musicapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicapp.data.database.dao.ArtistDao
import com.example.musicapp.data.database.dao.SongDao
import com.example.musicapp.data.database.entities.ArtistEntity
import com.example.musicapp.data.database.entities.SongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ArtistEntity::class, SongEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun artistDao(): ArtistDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.artistDao()?.insertUnknownArtist(
                                    ArtistEntity(name = "Неизвестно")
                                )
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val unknownArtist = INSTANCE?.artistDao()?.getUnknownArtist()
                                if (unknownArtist == null) {
                                    INSTANCE?.artistDao()?.insertUnknownArtist(
                                        ArtistEntity(name = "Неизвестно")
                                    )
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}