package com.example.notex.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.notex.model.Note
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "notes", indices = [Index(value = ["id"], unique = true)])
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val position: Long = 0,
    val id: String,
    val title: String,
    val content: String,
    val creationEpochDay: Long,
) {
    fun toNote() = Note(UUID.fromString(id), title, LocalDate.ofEpochDay(creationEpochDay), content)
}
