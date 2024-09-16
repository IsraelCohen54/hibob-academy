package com.hibob.nullability_exercises


/**
 * Modify the main function to print each user's email safely.
 * Use the Elvis operator to provide the "Email not provided" default string if the email is null.
 * Ensure your solution handles both user1 and user2 correctly.
 */
data class User(val name: String?, val email: String?)

fun main4() {
    val user1 = User("Alice", null)
    val user2 = User(null, "alice@example.com")


    println("user1 email: " + (user1.email ?: "user1 email is unknown"))
    println("user2 email: " + user2.email)

    // Task: Print user email or "Email not provided" if null
}
