package com.example.notex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteDao {
    @Query("SELECT * FROM notes ORDER BY position ASC")
    abstract fun observeNotes(): Flow<List<NoteEntity>>

    @Insert
    abstract suspend fun insert(note: NoteEntity)

    @Query("UPDATE notes SET title = :title, content = :content WHERE id = :id")
    protected abstract suspend fun updateFields(id: String, title: String, content: String): Int

    @Query("SELECT * FROM notes WHERE id = :id")
    protected abstract suspend fun find(id: String): NoteEntity?

    @Transaction
    open suspend fun update(id: String, title: String, content: String): NoteEntity? {
        if (updateFields(id, title, content) == 0) return null
        return find(id)
    }
}
