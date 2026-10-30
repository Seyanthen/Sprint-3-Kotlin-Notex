package com.example.notex.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notex.data.NoteRepository
import com.example.notex.model.Note
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch

enum class NoteScreen { NOTES, DETAIL, EDITOR }

data class EditorDraft(
    val title: String = "",
    val content: String = "",
    val originalTitle: String = "",
    val originalContent: String = "",
) {
    val changed: Boolean get() = title != originalTitle || content != originalContent
}

data class NotexUiState(
    val notes: List<Note> = emptyList(),
    val screen: NoteScreen = NoteScreen.NOTES,
    val selectedId: UUID? = null,
    val draft: EditorDraft = EditorDraft(),
    val titleError: Boolean = false,
    val showDiscardConfirmation: Boolean = false,
    val noteMissing: Boolean = false,
    val isLoading: Boolean = true,
    val loadError: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: Boolean = false,
) {
    val selectedNote: Note? get() = notes.find { it.id == selectedId }
}

class NotexViewModel(
    private val repository: NoteRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        NotexUiState(
            screen = savedState.get<String>("screen")?.let(NoteScreen::valueOf) ?: NoteScreen.NOTES,
            selectedId = savedState.get<String>("selectedId")?.let(UUID::fromString),
            draft = EditorDraft(
                title = savedState["title"] ?: "",
                content = savedState["content"] ?: "",
                originalTitle = savedState["originalTitle"] ?: "",
                originalContent = savedState["originalContent"] ?: "",
            ),
            titleError = savedState["titleError"] ?: false,
            showDiscardConfirmation = savedState["discard"] ?: false,
        ),
    )
    val state = mutableState.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadNotes()
    }

    fun loadNotes() {
        if (loadJob?.isActive == true) return
        mutableState.value = state.value.copy(isLoading = true, loadError = false)
        loadJob = viewModelScope.launch {
            repository.notes.catch { error ->
                if (error is CancellationException) throw error
                mutableState.value = state.value.copy(isLoading = false, loadError = true)
            }.collect { notes ->
                mutableState.value = state.value.copy(notes = notes, isLoading = false, loadError = false)
            }
        }
    }

    private fun publish(next: NotexUiState) {
        // Save UI values only; saved Note objects remain in the repository.
        savedState["screen"] = next.screen.name
        savedState["selectedId"] = next.selectedId?.toString()
        savedState["title"] = next.draft.title
        savedState["content"] = next.draft.content
        savedState["originalTitle"] = next.draft.originalTitle
        savedState["originalContent"] = next.draft.originalContent
        savedState["titleError"] = next.titleError
        savedState["discard"] = next.showDiscardConfirmation
        mutableState.value = next.copy(notes = state.value.notes, isLoading = state.value.isLoading, loadError = state.value.loadError)
    }

    fun addNote() = publish(NotexUiState(screen = NoteScreen.EDITOR))

    fun selectNote(id: UUID) {
        if (state.value.notes.any { it.id == id }) {
            publish(NotexUiState(screen = NoteScreen.DETAIL, selectedId = id))
        }
    }

    fun editNote() {
        val note = state.value.selectedNote ?: return
        publish(state.value.copy(
            screen = NoteScreen.EDITOR,
            draft = EditorDraft(note.title, note.content, note.title, note.content),
            titleError = false,
            noteMissing = false,
        ))
    }

    fun changeTitle(title: String) { if (!state.value.isSaving) publish(state.value.copy(
        draft = state.value.draft.copy(title = title),
        titleError = state.value.titleError && title.isBlank(),
    )) }

    fun changeContent(content: String) { if (!state.value.isSaving) publish(state.value.copy(draft = state.value.draft.copy(content = content))) }

    fun save() {
        val current = state.value
        if (current.screen != NoteScreen.EDITOR || current.showDiscardConfirmation || current.isSaving) return
        if (current.draft.title.isBlank()) {
            publish(current.copy(titleError = true))
            return
        }
        publish(current.copy(isSaving = true, saveError = false, noteMissing = false))
        viewModelScope.launch {
            try {
                val note = if (current.selectedId == null) {
                    repository.create(current.draft.title, current.draft.content)
                } else {
                    repository.update(current.selectedId, current.draft.title, current.draft.content)
                }
                if (note == null) {
                    publish(state.value.copy(isSaving = false, noteMissing = true))
                } else {
                    // The query Flow may emit after the write returns. Keep detail available immediately.
                    val notes = state.value.notes
                    mutableState.value = state.value.copy(notes = if (notes.any { it.id == note.id }) {
                        notes.map { if (it.id == note.id) note else it }
                    } else notes + note)
                    publish(NotexUiState(screen = NoteScreen.DETAIL, selectedId = note.id))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                publish(state.value.copy(isSaving = false, saveError = true))
            }
        }
    }

    // Root Back is left to Android's Activity dispatcher.
    fun back() {
        if (state.value.isSaving) return
        when (state.value.screen) {
            NoteScreen.NOTES -> Unit
            NoteScreen.DETAIL -> publish(NotexUiState())
            NoteScreen.EDITOR -> if (state.value.draft.changed) {
                publish(state.value.copy(showDiscardConfirmation = true))
            } else leaveEditor()
        }
    }

    fun keepEditing() = publish(state.value.copy(showDiscardConfirmation = false))

    fun discardDraft() {
        if (state.value.showDiscardConfirmation) leaveEditor()
    }

    private fun leaveEditor() {
        val id = state.value.selectedId
        publish(NotexUiState(
            screen = if (id != null && state.value.notes.any { it.id == id }) NoteScreen.DETAIL else NoteScreen.NOTES,
            selectedId = id,
        ))
    }
}
