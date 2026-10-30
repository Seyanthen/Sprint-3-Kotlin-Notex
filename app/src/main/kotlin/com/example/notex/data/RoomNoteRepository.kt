package com.example.notex.data

import com.example.notex.model.Note
import java.time.Clock
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.map

/** Room's suspend DAO and Flow queries run on its database executors. */
class RoomNoteRepository(
    private val dao: NoteDao,
    private val clock: Clock = Clock.systemDefaultZone(),
) : NoteRepository {
    override val notes = dao.observeNotes().map { rows -> rows.map { it.toNote() } }

    override suspend fun create(title: String, content: String): Note {
        val row = NoteEntity(id = UUID.randomUUID().toString(), title = title,
            content = content, creationEpochDay = LocalDate.now(clock).toEpochDay())
        dao.insert(row)
        return row.toNote()
    }

    override suspend fun update(id: UUID, title: String, content: String): Note? =
        dao.update(id.toString(), title, content)?.toNote()
}
