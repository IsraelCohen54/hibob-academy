package com.hibob

fun isContainsSpecialCharacterExcludingUnderscore(s: String): Boolean {
    val regex = "[^a-zA-Z0-9_]".toRegex()
    return regex.containsMatchIn(s)
}

fun isValidIdentifier(s: String): Boolean {
    // vacant
    if (s == "" ) {
        return false
    }

    // is first char start with number
    if (s[0] in '0'..'9') {
        return false
    }

    return isContainsSpecialCharacterExcludingUnderscore(s)
}

fun main(args: Array<String>) {
    println(isValidIdentifier("name"))   // true
    println(isValidIdentifier("_name"))  // true
    println(isValidIdentifier("_12"))    // true
    println(isValidIdentifier(""))       // false
    println(isValidIdentifier("012"))    // false
    println(isValidIdentifier("no$"))    // false
}