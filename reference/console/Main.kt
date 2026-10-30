/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
*
*   AI was used in this project in the following ways:
*   - Main menu logic planning
*   - Kotlin to Android UI conversion
*   - Readme Instructions for Build and Use & Development Environment
*
*   This Main.kt does not run in the android app, but has been preserved
*   for class presentation purposes
* */

// import libraries
import java.time.LocalDate

// Main Function
fun main() {
    // create mutable list of Notes
    // STRETCH CHALLENGE: Create and modify a Collection
    val notes = mutableListOf<Note>()

    // UI experience
    println("Welcome to Notex!")

    // create an example note
    val exNote1 = Note()
    exNote1.title = "Example note 1"
    exNote1.date = LocalDate.now()
    exNote1.content = "This is an example note for the user\ncreated at program startup"
    notes.add(exNote1)

    // Main menu functionality
    while (true) {
        // user prompt
        println("\nMain Menu")
        println("1. Create a new note")
        println("2. Edit an existing note")
        println("3. Print an existing note")
        println("4. Exit")
        println("Choose an option: ")

        // user input for function
        val choice = readlnOrNull()?.trim() ?: return
        // use of WHEN stretch challenge
        when (choice) {
            // New note selection
            "1" -> { // create and add a new note to the notes list
                val note = Note()
                note.newNote()
                notes.add(note)
            }
            // Edit note selection
            "2" -> { // edit the content of the selected note
                val note = selectNote(notes)
                if (note != null) { // valid note check
                    note.editNote()
                }
            }
            // Print note selection
            "3" -> { // print the title and content of the selected note
                val note = selectNote(notes)
                if (note != null) { // valid note check
                    note.printNote()
                }
            }
            // exit program selection
            "4" -> {
                println("Goodbye!")
                return
            }
            // invalid selection
            else -> println("Invalid option. Enter 1, 2, 3, or 4.")
        }
    }
} // end of main()

// helper function to choose existing note
fun selectNote (notes: List<Note>): Note? {
    // empty note list check
    if (notes.isEmpty()){
        println("No notes created yet.")
        return null
    }

    // list notes to choose from
    println("\nYour Notes:")
    for ((index, note) in notes.withIndex()) {
        println("${index + 1}: ${note.title}")
    }

    // prompt user to select note - convert input to int or null
    println("Enter the note number you wish to select: ")
    val number = readlnOrNull()?.trim()?.toIntOrNull()

    // safety check for invalid input
    if (number == null || number !in 1..notes.size) {
        println("Invalid note number.")
        return null
    }

    // return the selected note with index adjustment (-1) for List
    return notes[number -1]
}

// Kotlin basic Class
class Note {
    var title: String = ""
    var date: LocalDate? = null
    var content: String = ""

    // Create a new note
    fun newNote() {
        println("\nLet's make your new note. What should it be titled? ")
        title = readln()
        date = LocalDate.now()
        println ("What does your note say?")
        content = readln()
        println("Successfully created note!")
    }

    // Print a new note
    fun printNote() {
        println("$title ($date)")
        println(content)
    }

    // Edit an existing note
    fun editNote() {
        // Input check and note title editing
        while (true) {
            println("Do you want to edit the title \"${title}\"? Y/N")
            val x = readln()
            // User chooses to edit the title
            if (x == "Y" || x == "y"){
                println("What is the new title?")
                title = readln()
                break
            }
            // User chooses to not edit the title
            else if (x == "N" || x == "n") {
                break
            }
            // User puts in an invalid input
            else {
                println("That is an invalid value!")
            }
        }

        // input check and note content editing
        while (true) {
            println("Do you want to edit the content? Y/N")
            println("\"${content}\"")
            val x = readln()
            // user chooses to edit the content
            if (x == "Y" || x == "y"){
                println("What is the new content?")
                content = readln()
                break
            }
            // user chooses not to edit the content
            else if (x == "N" || x == "n") {
                break
            }
            // user enters invalid input
            else {
                println("That is an invalid value!")
            }
        }
        // report success
        println("Successfully edited note!\n")
    }
}