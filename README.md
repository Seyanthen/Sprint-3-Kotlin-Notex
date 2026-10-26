# Notex (1.0)

A Kotlin console note-taking app that lets users create notes, edit their titles and content, and print existing notes. Each note includes a title, creation date, and content. Notes are stored in a mutable list during the current session and are not saved after the program exits.

Write one or more programs in Kotlin that demonstrates the following 5 things:
* [x] Variables (mutable and immutable)
* [x] Conditionals
* [x] Loops
* [x] Functions
* [x] Classes

Stretch Challenges (select one):
* [x] Modify your program to demonstrate the creation and modification of collections.

## Instructions for Build and Use

Steps to build and/or run the software:

1. Install JDK 25 and open this folder in IntelliJ IDEA.
2. Load `build.gradle.kts` as a Gradle project when IntelliJ prompts you. Use the Gradle wrapper and JDK 25 for the Gradle JVM.
3. In the project terminal, run `.\gradlew.bat build` to compile and package the app. The first build downloads Gradle and Kotlin dependencies and requires internet access.

On macOS or Linux, use `./gradlew build` instead.

Instructions for using the software:

1. Run `.\gradlew.bat run` in the project terminal, or run the Gradle `application > run` task in IntelliJ.
2. The app displays a welcome message and a main menu. An example note is available at startup.
3. Choose `1` to create a note by entering its title and content. The creation date is set automatically.
4. Choose `2` to edit a note. Select its number from the list, then answer `Y` or `N` to change its title and content.
5. Choose `3` to print a note. Select its number to display its title, creation date, and content in the console.
6. Choose `4` to exit. Notes created or edited during the session are not saved between runs.

On macOS or Linux, use `./gradlew run` instead.

## Development Environment

To recreate the development environment, you need the following software and/or libraries with the specified versions:

* IntelliJ IDEA with Kotlin and Gradle support enabled
* JDK 25 (the project uses a Java 25 toolchain)
* Kotlin 2.3.21 (downloaded automatically by Gradle)
* Gradle 9.3.0 (provided by the checked-in Gradle wrapper)

## Useful Websites to Learn More

I found these websites useful in developing this software:

* [W3 Schools Tutorial](https://www.w3schools.com/kotlin/index.php)

## Future Work

The following items I plan to fix, improve, and/or add to this project in the future:

* [ ] Implement Android App UI functionality
