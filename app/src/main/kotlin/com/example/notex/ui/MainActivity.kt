package com.example.notex.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notex.data.RoomNoteRepository
import com.example.notex.data.NotexDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NotexViewModel = viewModel(factory = viewModelFactory {
                initializer { NotexViewModel(RoomNoteRepository(NotexDatabase.getInstance(applicationContext).noteDao()), createSavedStateHandle()) }
            })
            val state by viewModel.state.collectAsStateWithLifecycle()
            NotexTheme {
                NotexScreen(
                    state = state,
                    onAdd = viewModel::addNote,
                    onSelect = viewModel::selectNote,
                    onEdit = viewModel::editNote,
                    onTitleChange = viewModel::changeTitle,
                    onContentChange = viewModel::changeContent,
                    onSave = viewModel::save,
                    onBack = viewModel::back,
                    onDiscard = viewModel::discardDraft,
                    onKeepEditing = viewModel::keepEditing,
                    onRetry = viewModel::loadNotes,
                )
            }
        }
    }
}
