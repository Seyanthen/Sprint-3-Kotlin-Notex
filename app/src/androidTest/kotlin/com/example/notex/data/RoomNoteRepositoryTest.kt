package com.example.notex.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomNoteRepositoryTest {
    @Test fun fileBackedNotesSurviveReopenAndEditingPreservesIdentityDateAndOrder() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "persistence-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder(context, NotexDatabase::class.java, name).build()
        var database = open()
        try {
            val repository = RoomNoteRepository(database.noteDao(), Clock.fixed(
                Instant.parse("2020-02-29T12:00:00Z"), ZoneOffset.UTC))
            assertTrue(repository.notes.first().isEmpty())
            val first = repository.create("Same title", "First\nSecond")
            val second = repository.create("Same title", "")
            assertEquals(LocalDate.of(2020, 2, 29), first.creationDate)
            database.close()
            database = open()
            val reopened = RoomNoteRepository(database.noteDao())
            assertEquals(listOf(first, second), reopened.notes.first())
            assertEquals(first.copy(title = "Edited", content = "Changed"),
                reopened.update(first.id, "Edited", "Changed"))
            assertNull(reopened.update(UUID.randomUUID(), "Missing", ""))
            database.close()
            database = open()
            assertEquals(listOf(first.copy(title = "Edited", content = "Changed"), second),
                RoomNoteRepository(database.noteDao()).notes.first())
        } finally {
            database.close()
            context.deleteDatabase(name)
        }
    }
}
