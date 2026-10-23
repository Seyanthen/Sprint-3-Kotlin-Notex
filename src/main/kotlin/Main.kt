/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
* */
// import libraries
import java.time.LocalDate

fun main() {
    println("Welcome to Notex!")

    val firstNote = Note()

    firstNote.newNote()
    firstNote.printNote()
    firstNote.editNote()
    firstNote.printNote()



} // end of main()

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
        println("Successfully created note!\n")
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
            println("Do you want to edit the title? Y/N")
            val x = readln()
            if (x == "Y" || x == "y"){
                println("What is the new title?")
                title = readln()
                break
            } else if (x == "N" || x == "n") {
                break
            } else {
                println("That is an invalid value!")
            }
        }

        // input check and note content editing
        while (true) {
            println("Do you want to edit the content? Y/N")
            val x = readln()
            if (x == "Y" || x == "y"){
                println("What is the new content?")
                content = readln()
                break
            } else if (x == "N" || x == "n") {
                break
            } else {
                println("That is an invalid value!")
            }
        }
        println("Successfully edited note!\n")
    }
}