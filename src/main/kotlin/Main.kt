/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
* */

fun main() {
    // This is a single line comment
    println("Welcome to Notex!")

    // call to another function
    print()

    // variables do not need to be declared by type
    var name = "Jeff" // var variables can change
    val birthyear = 1976 // val constants cannot change

    // multiple ways to concatenate
    println("Hello " + name)
    name = "Bryan"
    println("$name was born in $birthyear")

    // basic loop functionality and break function
    var i = 0
    while (i < 10) {
        println(i)
        i++
        if (i == 4) {
            break
        }
    }

    // basic array features
    val carsList = arrayOf("Volvo", "BMW", "Ford", "Mazda")
    carsList[0] = "Toyota"
    println(carsList[0])


}



// First test function
fun print() {
    println("This is a test file")
}