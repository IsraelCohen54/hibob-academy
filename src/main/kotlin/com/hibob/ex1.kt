package com.hibob

fun printEvenOrOdd (num: Int) {
    if (num % 2 == 0) {
        println("even")
    }
    else println("odd")
}

fun isEqual(num1: Int, num2: Int) {
    if (num1 == num2) {
        println("equal")
    }
    else println("not equal")
}

fun max (num1: Int, num2: Int) = if (num1 > num2) num1 else num2

fun multiplication(a: Int = 1, b: Int = 1): Int = a * b

fun main3() {
    println("Hello world!")

    // test:
    printEvenOrOdd(1)
    printEvenOrOdd(2)

    isEqual(2, 3)
    isEqual(2, 2)

    println(max(1, 2))
    println(max(2, 1))

    println(multiplication())
    println(multiplication(a = 2))
    println(multiplication(b = 2))
    println(multiplication(a = 3, b = 2))
    println(multiplication(b = 3, a = 2))
}
