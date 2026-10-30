package com.example.notex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1, exportSchema = true)
abstract class NotexDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var instance: NotexDatabase? = null

        fun getInstance(context: Context): NotexDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, NotexDatabase::class.java, "notex.db",
            ).build().also { instance = it }
        }
    }
}
