package com.example.notex.ui

import androidx.lifecycle.SavedStateHandle
import com.example.notex.data.InMemoryNoteRepository
import com.example.notex.data.NoteRepository
import com.example.notex.model.Note
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class NotexViewModelTest {
    private val repository = InMemoryNoteRepository()
    private val savedState = SavedStateHandle()
    private lateinit var viewModel: NotexViewModel

    @Before fun setUp() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = NotexViewModel(repository, savedState)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun createsMultilineNoteAndOpensDetailWithoutDuplicateSave() = runTest {
        assertTrue(viewModel.state.value.notes.isEmpty())
        viewModel.addNote()
        assertEquals(EditorDraft(), viewModel.state.value.draft)
        viewModel.changeTitle("Ideas")
        viewModel.changeContent("First line\nSecond line")
        assertTrue(repository.notes.value.isEmpty())
        viewModel.save()
        viewModel.save()
        val note = repository.notes.value.single()
        assertEquals("Ideas", note.title)
        assertEquals("First line\nSecond line", note.content)
        assertEquals(note, viewModel.state.value.selectedNote)
        assertEquals(NoteScreen.DETAIL, viewModel.state.value.screen)
        viewModel.back()
        assertEquals(NoteScreen.NOTES, viewModel.state.value.screen)
        assertNull(viewModel.state.value.selectedId)
    }

    @Test fun rejectsBlankTitleButAllowsEmptyContent() = runTest {
        viewModel.addNote()
        viewModel.changeTitle(" \t\n")
        viewModel.save()
        assertTrue(viewModel.state.value.titleError)
        assertEquals(NoteScreen.EDITOR, viewModel.state.value.screen)
        assertTrue(repository.notes.value.isEmpty())
        viewModel.changeTitle("Title")
        assertFalse(viewModel.state.value.titleError)
        viewModel.save()
        assertEquals("", repository.notes.value.single().content)
    }

    @Test fun editsSelectedIdAfterListReorderingAndPreservesDate() = runTest {
        val first = repository.create("Same title", "First")
        val second = repository.create("Same title", "Second")
        val notes = MutableStateFlow(listOf(first, second))
        val reordered = object : NoteRepository {
            override val notes = notes
            override suspend fun create(title: String, content: String): Note = error("Editing must not create")
            override suspend fun update(id: UUID, title: String, content: String): Note? {
                val updated = notes.value.find { it.id == id }?.copy(title = title, content = content)
                notes.value = notes.value.map { if (it.id == id) updated!! else it }
                return updated
            }
        }
        val vm = NotexViewModel(reordered, SavedStateHandle())
        vm.selectNote(second.id)
        vm.editNote()
        assertEquals("Second", vm.state.value.draft.content)
        notes.value = listOf(second, first)
        vm.changeTitle("Changed")
        vm.changeContent("")
        vm.save()
        assertEquals(listOf(second.copy(title = "Changed", content = ""), first), notes.value)
        assertEquals(second.id, vm.state.value.selectedId)
        assertEquals(second.creationDate, vm.state.value.selectedNote!!.creationDate)
    }

    @Test fun backAndCancelAskBeforeDiscardingAndNeverMutateSavedNote() = runTest {
        val note = repository.create("Saved", "Original")
        viewModel.selectNote(note.id)
        viewModel.editNote()
        viewModel.changeTitle("Unsaved")
        viewModel.changeContent("Draft")
        viewModel.back() // Both Back and Cancel send this event.
        assertTrue(viewModel.state.value.showDiscardConfirmation)
        assertEquals(listOf(note), repository.notes.value)
        viewModel.keepEditing()
        assertFalse(viewModel.state.value.showDiscardConfirmation)
        assertEquals("Draft", viewModel.state.value.draft.content)
        viewModel.back()
        viewModel.discardDraft()
        assertEquals(NoteScreen.DETAIL, viewModel.state.value.screen)
        assertEquals(note, viewModel.state.value.selectedNote)
        assertEquals(EditorDraft(), viewModel.state.value.draft)
        assertEquals(listOf(note), repository.notes.value)
    }

    @Test fun cancelsNewDraftWithoutCreatingNote() = runTest {
        viewModel.addNote()
        viewModel.changeContent("Unsaved")
        viewModel.back()
        assertTrue(viewModel.state.value.showDiscardConfirmation)
        viewModel.discardDraft()
        assertEquals(NoteScreen.NOTES, viewModel.state.value.screen)
        assertTrue(repository.notes.value.isEmpty())
    }

    @Test fun unchangedOrRevertedDraftLeavesWithoutConfirmation() = runTest {
        viewModel.addNote()
        viewModel.back()
        assertEquals(NoteScreen.NOTES, viewModel.state.value.screen)
        val note = repository.create("Title", "Content")
        viewModel.selectNote(note.id)
        viewModel.editNote()
        viewModel.changeTitle("Changed")
        viewModel.changeTitle(note.title)
        viewModel.back()
        assertEquals(NoteScreen.DETAIL, viewModel.state.value.screen)
        assertFalse(viewModel.state.value.showDiscardConfirmation)
        assertEquals(listOf(note), repository.notes.value)
    }

    @Test fun restoresSelectionDraftValidationAndDiscardDialogFromSavedValues() = runTest {
        val note = repository.create("Title", "Content")
        viewModel.selectNote(note.id)
        viewModel.editNote()
        viewModel.changeTitle("")
        viewModel.changeContent("Line one\nLine two")
        viewModel.save()
        viewModel.back()
        val restoredHandle = SavedStateHandle(savedState.keys().associateWith { savedState.get<Any?>(it) })
        val restored = NotexViewModel(repository, restoredHandle)
        assertEquals(viewModel.state.value, restored.state.value)
        restored.keepEditing()
        restored.save()
        assertTrue(restored.state.value.titleError)
        restored.changeTitle("Updated")
        restored.save()
        assertEquals(note.copy(title = "Updated", content = "Line one\nLine two"), repository.notes.value.single())
        val detailRestored = NotexViewModel(repository, restoredHandle)
        assertEquals(NoteScreen.DETAIL, detailRestored.state.value.screen)
        assertEquals(note.id, detailRestored.state.value.selectedId)
    }

    @Test fun pendingSaveIgnoresRepeatedTapsAndFailureRetainsDraftForRetry() = runTest {
        val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
        var attempts = 0
        val backing = InMemoryNoteRepository()
        val failing = object : NoteRepository {
            override val notes = backing.notes
            override suspend fun create(title: String, content: String): Note {
                attempts++
                if (attempts == 1) {
                    gate.await()
                    throw java.io.IOException("Disk full")
                }
                return backing.create(title, content)
            }
            override suspend fun update(id: UUID, title: String, content: String): Note? = null
        }
        val vm = NotexViewModel(failing, SavedStateHandle())
        vm.addNote()
        vm.changeTitle("Keep me")
        vm.changeContent("Draft")
        vm.save()
        vm.save()
        vm.back()
        assertEquals(1, attempts)
        assertTrue(vm.state.value.isSaving)
        assertEquals(NoteScreen.EDITOR, vm.state.value.screen)
        gate.complete(Unit)
        assertFalse(vm.state.value.isSaving)
        assertTrue(vm.state.value.saveError)
        assertEquals("Keep me", vm.state.value.draft.title)
        assertEquals("Draft", vm.state.value.draft.content)
        assertTrue(backing.notes.value.isEmpty())
        vm.save()
        assertEquals(2, attempts)
        assertEquals(1, backing.notes.value.size)
        assertEquals(NoteScreen.DETAIL, vm.state.value.screen)
    }

    @Test fun loadingFailureCanRetryWithoutLosingRestoredDraft() = runTest {
        var fail = true
        val repository = object : NoteRepository {
            override val notes = kotlinx.coroutines.flow.flow<List<Note>> {
                if (fail) throw java.io.IOException("Unavailable")
                emit(emptyList())
            }
            override suspend fun create(title: String, content: String): Note = error("Unused")
            override suspend fun update(id: UUID, title: String, content: String): Note? = null
        }
        val vm = NotexViewModel(repository, SavedStateHandle(mapOf(
            "screen" to "EDITOR", "title" to "Restored draft")))
        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.loadError)
        fail = false
        vm.loadNotes()
        assertFalse(vm.state.value.loadError)
        assertEquals("Restored draft", vm.state.value.draft.title)
    }
}
