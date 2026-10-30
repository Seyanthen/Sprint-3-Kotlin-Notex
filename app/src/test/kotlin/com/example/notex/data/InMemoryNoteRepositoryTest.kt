package com.example.notex.data

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.test.runTest

class InMemoryNoteRepositoryTest {
    private val repository = InMemoryNoteRepository(
        Clock.fixed(Instant.parse("2026-10-27T12:00:00Z"), ZoneOffset.UTC),
    )

    @Test
    fun creationPublishesUniqueNotesWithCreationDate() = runTest {
        assertTrue(repository.notes.value.isEmpty())
        val first = repository.create("Same title", "First")
        val second = repository.create("Same title", "Second")

        assertNotEquals(first.id, second.id)
        assertEquals(LocalDate.of(2026, 10, 27), first.creationDate)
        assertEquals(listOf(first, second), repository.notes.value)
    }

    @Test
    fun updateUsesIdAndPreservesIdentityDateAndEarlierSnapshot() = runTest {
        val first = repository.create("Same title", "First")
        val second = repository.create("Same title", "Second")
        val before = repository.notes.value
        val updated = repository.update(first.id, "Changed", "New content")

        assertEquals(first.copy(title = "Changed", content = "New content"), updated)
        assertEquals(listOf(updated, second), repository.notes.value)
        assertEquals(listOf(first, second), before)
    }

    @Test
    fun unknownIdDoesNotChangeNotes() = runTest {
        repository.create("Title", "Content")
        val before = repository.notes.value

        assertNull(repository.update(UUID.randomUUID(), "Changed", "Changed"))
        assertSame(before, repository.notes.value)
    }

    @Test
    fun editingEitherFieldCanPreserveTheOtherAndAcceptEmptyText() = runTest {
        val original = repository.create("Title", "Content")
        repository.update(original.id, "", original.content)
        assertEquals(original.copy(title = ""), repository.notes.value.single())
        repository.update(original.id, "", "")
        assertEquals(original.copy(title = "", content = ""), repository.notes.value.single())
    }
}
