/*
*   Kotlin Note-Taking Android App Notex
*   CSE 310 Sprint 3 Project
*   Joshua Adams
* */

fun main() {
    println("Welcome to Notex!")

    // Basic Classes and Objects
    val myCar = Car()
    myCar.brand = "Honda"
    myCar.model = "Civic"
    myCar.year = 2009

    val yourCar = Car()
    yourCar.brand = "Toyota"
    yourCar.model = "Corolla"
    yourCar.year = 2013

    printCar(myCar)
    printCar(yourCar)

    // Kotlin Constructor
    val hisCar = ConCar("Ford", "F150", 2015)
    printConCar(hisCar)

    myCar.drive()
    myCar.speed(180)
    yourCar.drive()
    yourCar.speed(190)


} // end of main()

// Kotlin basic Class
class Car {
    var brand = ""
    var model = ""
    var year = 0

    // Class function
    fun drive() {
        println("Vroom!")
    }

    fun speed (topSpeed: Int){
        println("Top speed: $topSpeed")
    }
}

// Kotlin Constructor
class ConCar (var brand: String, var model: String, var year: Int)

// Print function for classes
fun printCar(car: Car) {
    println("${car.brand} ${car.model} ${car.year}")
}
fun printConCar(car: ConCar) {
    println("${car.brand} ${car.model} ${car.year}")
}