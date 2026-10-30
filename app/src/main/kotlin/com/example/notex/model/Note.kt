package com.example.notex.model

import java.time.LocalDate
import java.util.UUID

data class Note(
    val id: UUID,
    val title: String,
    val creationDate: LocalDate,
    val content: String,
)
