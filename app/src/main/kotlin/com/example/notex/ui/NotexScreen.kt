package com.example.notex.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notex.R
import com.example.notex.model.Note
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotexScreen(
    state: NotexUiState,
    onAdd: () -> Unit,
    onSelect: (UUID) -> Unit,
    onEdit: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit,
    onRetry: () -> Unit,
) {
    BackHandler(enabled = state.screen != NoteScreen.NOTES, onBack = onBack)
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(when (state.screen) {
                        NoteScreen.NOTES -> R.string.notes
                        NoteScreen.DETAIL -> R.string.note_detail
                        NoteScreen.EDITOR -> if (state.selectedId == null) R.string.add_note else R.string.edit_note
                    }))
                },
                navigationIcon = {
                    if (state.screen != NoteScreen.NOTES) {
                        TextButton(onClick = onBack, enabled = !state.isSaving) { Text(stringResource(R.string.back)) }
                    }
                },
                actions = {
                    if (state.screen == NoteScreen.DETAIL && state.selectedNote != null) {
                        TextButton(onClick = onEdit) { Text(stringResource(R.string.edit)) }
                    }
                },
            )
        },
        floatingActionButton = {
            if (state.screen == NoteScreen.NOTES) {
                ExtendedFloatingActionButton(onClick = onAdd) { Text(stringResource(R.string.add_note)) }
            }
        },
    ) { insets ->
        if (state.isLoading && state.screen != NoteScreen.EDITOR) {
            Column(Modifier.padding(insets).padding(20.dp)) {
                CircularProgressIndicator()
                Text(stringResource(R.string.loading))
            }
        } else if (state.loadError && state.screen != NoteScreen.EDITOR) {
            Column(Modifier.padding(insets).padding(20.dp)) {
                Text(stringResource(R.string.load_error))
                Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            }
        } else when (state.screen) {
            NoteScreen.NOTES -> NotesList(state.notes, onSelect, Modifier.padding(insets))
            NoteScreen.DETAIL -> NoteDetail(state.selectedNote, Modifier.padding(insets))
            NoteScreen.EDITOR -> NoteEditor(
                state, onTitleChange, onContentChange, onSave, onBack, Modifier.padding(insets),
            )
        }
    }
    if (state.showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = onKeepEditing,
            title = { Text(stringResource(R.string.discard_title)) },
            text = { Text(stringResource(R.string.discard_message)) },
            confirmButton = {
                TextButton(onClick = onDiscard) { Text(stringResource(R.string.discard)) }
            },
            dismissButton = {
                TextButton(onClick = onKeepEditing) { Text(stringResource(R.string.keep_editing)) }
            },
        )
    }
}

@Composable
private fun NotesList(notes: List<Note>, onSelect: (UUID) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (notes.isEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.no_notes), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.empty_hint))
                }
            }
        }
        items(notes, key = { it.id.toString() }) { note ->
            Card(onClick = { onSelect(note.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(note.title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    CreationDate(note)
                    Text(
                        note.content.ifEmpty { stringResource(R.string.empty_content) },
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteDetail(note: Note?, modifier: Modifier = Modifier) {
    SelectionContainer {
        Column(
            modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (note == null) {
                Text(stringResource(R.string.note_missing))
            } else {
                Text(note.title, style = MaterialTheme.typography.headlineMedium)
                CreationDate(note)
                Text(note.content.ifEmpty { stringResource(R.string.empty_content) }, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun CreationDate(note: Note) {
    Text(
        stringResource(R.string.created_on, note.creationDate.toString()),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun NoteEditor(
    state: NotexUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        state.selectedNote?.let { CreationDate(it) }
        OutlinedTextField(
            enabled = !state.isSaving,
            value = state.draft.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.title_label)) },
            singleLine = true,
            isError = state.titleError,
            supportingText = { if (state.titleError) Text(stringResource(R.string.title_required)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            enabled = !state.isSaving,
            value = state.draft.content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.content_label)) },
            minLines = 6,
            maxLines = 10,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )
        if (state.saveError) {
            Text(stringResource(R.string.save_error), color = MaterialTheme.colorScheme.error)
        }
        if (state.noteMissing) {
            Text(stringResource(R.string.save_missing), color = MaterialTheme.colorScheme.error)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, enabled = !state.isSaving, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.cancel)) }
            Button(onClick = onSave, enabled = !state.isSaving, modifier = Modifier.weight(1f)) {
                Text(stringResource(if (state.isSaving) R.string.saving else R.string.save))
            }
        }
    }
}
