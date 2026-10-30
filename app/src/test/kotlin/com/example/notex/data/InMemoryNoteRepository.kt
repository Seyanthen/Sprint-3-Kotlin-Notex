package com.example.notex.data

import com.example.notex.model.Note
import java.time.Clock
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Session-only storage. All mutations publish new snapshots and are serialized. */
class InMemoryNoteRepository(
    private val clock: Clock = Clock.systemDefaultZone(),
) : NoteRepository {
    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    override val notes = mutableNotes.asStateFlow()

    override suspend fun create(title: String, content: String): Note {
        val note = Note(UUID.randomUUID(), title, LocalDate.now(clock), content)
        mutableNotes.value = buildList {
            addAll(mutableNotes.value)
            add(note)
        }
        return note
    }

    override suspend fun update(id: UUID, title: String, content: String): Note? {
        val current = mutableNotes.value
        val index = current.indexOfFirst { it.id == id }
        if (index == -1) return null

        val updated = current[index].copy(title = title, content = content)
        mutableNotes.value = buildList {
            addAll(current)
            set(index, updated)
        }
        return updated
    }
}
