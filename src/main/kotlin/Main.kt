/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
* */

fun main() {
    // This is a single line comment
    println("Welcome to Notex!")

    // variables do not need to be declared by type
    var name = "Jeff" // var variables can change
    val birthyear = 1976 // val constants cannot change

    // call to another function
    printNameYear(name, birthyear)

    // function with return values
    println(ageRange(birthyear))
    println(ageRange(2004))
    println(ageRange(1940))
    println(myMath(birthyear, 24))
    println()

    // multiple ways to concatenate
    println("Hello " + name)
    name = "Bryan"
    println("$name was born in $birthyear")
    println()

    // basic loop functionality and break function
    var i = 0
    while (i < 10) {
        // println(i)
        i++
        if (i == 4) {
            break
        }
    }

    // basic array features
    val carsList = arrayOf("Volvo", "BMW", "Ford", "Mazda")
    carsList[0] = "Toyota"
    println(carsList[0])


} // end of main()

// First test function
fun printNameYear(text: String, number: Number) {
    println(text + number)
}

// function with return
fun ageRange(birthyear: Int): String {
    if (birthyear < 1980 && birthyear > 1970) {
        return "Disco party time"
    } else if (birthyear > 1980) {
        return "Newbies"
    } else {
        return "Too old!"
    }
}

// shorthand return
fun myMath(x: Int, y: Int) = x + y