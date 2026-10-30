package com.example.notex.data

import com.example.notex.model.Note
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    /** Current notes and subsequent snapshots, in creation order. */
    val notes: Flow<List<Note>>

    suspend fun create(title: String, content: String): Note

    /** Replaces title/content, preserving ID and creation date. Missing IDs return null. */
    suspend fun update(id: UUID, title: String, content: String): Note?
}
