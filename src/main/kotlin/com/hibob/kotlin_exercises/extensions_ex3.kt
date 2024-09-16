package com.hibob.kotlin_exercises

import kotlin.math.pow

// q1:
//fun sum(list: List<Int>):Int {
//    var result = 0
//    for (i in list) {
//        result += i
//    }
//    return result
//}

// Ans_1:
fun List<Int>.sum() : Int{
    var sum = 0
    this.forEach { item ->
        sum += item
    }
    return sum
}

//import kotlin.math.pow
infix fun Number.toPowerOf(exponent: Number): Double {
    return this.toDouble().pow(exponent.toDouble())
}

fun main14() {
    val numList = listOf(1,2,3,4,5)

    println(numList.sum())
    println(3.toPowerOf(2))
    println(2.toPowerOf(0))
}
