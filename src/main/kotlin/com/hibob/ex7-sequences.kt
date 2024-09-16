package com.hibob

fun main2() {
    ex5()
}

fun ex1() {
    // change the program to
    // 1. reuse the filter / map function
    // 2. println each call to track the diffs between List and Seq

    val list = listOf(1, 2, 3, 4)

    fun isEven(i: Int) = i % 2 == 0
    fun sq(i: Int) = i * i

    val maxOddSquareList = list
        .map {
            println("Squaring $it")
            sq(it)
        }
        .filter {
            println("Filtering $it")
            isEven(it)
        }
        .find { it == 4 }

    val maxOddSquareSequence = list
        .asSequence()
        .map {
            println("Squaring $it")
            sq(it)
        }
        .filter {
            println("Filtering $it")
            isEven(it)
        }
        .find { it == 4 }

    println("Found $maxOddSquareList from list and $maxOddSquareSequence from sequence")
}


fun ex2() {
    // Q: how many times filterFunc was called -> A: 0, at least until a terminal action is applied to the sequence

    fun filterFunc(it: Int): Boolean {
        println("filterFunc was called")
        return it < 3
    }
    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter { filterFunc(it) }
}

fun ex3() {
    // create the list of the first 10 items of (2, 4, 8, 16, 32 ...) seq

    fun crazySeq(): Sequence<Int> = sequence {
        var count = 1
        var a = 2
        println("Returning element #${count++}")
        yield(a)

        while (true) {
            a *= 2
            println("Returning element #${count++}")
            yield(a)
        }
    }

//    val tenFirstItems1 = generateSequence(2) { it * 2 }.take(10).toList()
//    println(tenFirstItems1)
    val tenFirstItems2 = crazySeq().take(10).toList()
    println(tenFirstItems2)
//    val tenFirstItems3 = sequenceOf(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024).toList()
//    println(tenFirstItems3)
}

fun ex4() {
    // create the list of the first 10 items of the Fibonacci seq

    fibonacciSequence().take(10).forEach(::println)
}

fun fibonacciSequence(): Sequence<Int> = sequence {
    var a = 1
    var b = 1
    yield(a)  // First Fibonacci number
    yield(b)  // Second Fibonacci number

    while (true) {
        val next = a + b
        yield(next)  // Yield the next Fibonacci number
        a = b
        b = next
    }
}

fun ex5() {
    // try to minimize the number of operations:

    val engToHeb: Map<String, String> = mapOf(
        "today" to "היום",
        "was" to "היה",
        "good" to "טוב",
        "day" to "יום",
        "for" to "בשביל",
        "walking" to "ללכת",
        "in" to "ב",
        "the" to "הבלבלה",
        "park" to "פארק",
        "sun" to "שמש",
        "was" to "הייתה",
        "shining" to "זורחת",
        "and" to "ו",
        "birds" to "ציפורים",
        "were" to "היו",
        "chirping" to "מצייצות",
    )

    println(
        "today was a good day for walking in the park. sun was shining and birds were chirping"
            .splitToSequence(" ")
            .mapNotNull { engToHeb[it] }
            .filter {
                val isValid = it.length <= 3
                if (isValid) println(it)
                isValid
            }
            .take(5)
            .count() > 4
    )
}