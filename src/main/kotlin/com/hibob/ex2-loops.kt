package com.hibob

fun isContainDolarSymbol(s: String): Boolean = "$" !in s

fun isContainOnlyNumbers(s: String): Boolean {
    var numberOnly = true
    for (char in s) {
        if (char <= '9' && char >= '0') continue else numberOnly = false
    }
    if (numberOnly) return false else return true
}

fun isValidIdentifier(s: String): Boolean {
    // vacant
    if (s == "" ) {
        return false
    }

    // is first char start with number
    if (s[0] <= '9' && s[0] >= '0') {
        return false
    }

    val res = isContainOnlyNumbers(s)
    if (res == false) return false

    return isContainDolarSymbol(s)
}

fun main(args: Array<String>) {
    println(isValidIdentifier("name"))   // true
    println(isValidIdentifier("_name"))  // true
    println(isValidIdentifier("_12"))    // true
    println(isValidIdentifier(""))       // false
    println(isValidIdentifier("012"))    // false
    println(isValidIdentifier("no$"))    // false
}