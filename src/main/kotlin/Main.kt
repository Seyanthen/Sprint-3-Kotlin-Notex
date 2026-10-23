/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
* */
// import libraries
import java.time.LocalDate

fun main() {
    println("Welcome to Notex!")

    println ("Let's make your first note. What should it be titled? ")
    val firstNote = Note()
    firstNote.title = readln()

    var toDate = LocalDate.now()
    firstNote.date = toDate

    println("Great, now what is the content of your note?")
    firstNote.content = readln()

    println("Successfully created note!")
    println(firstNote.title + " " + firstNote.date)
    println(firstNote.content)

} // end of main()

// Kotlin basic Class
class Note {
    var title: String = ""
    var date: LocalDate? = null
    var content: String = ""
}