package com.hibob.academy.dao

import java.time.LocalDate

data class Example(val id: Long, val companyId: Long, val data: String)

data class Owner(
    val id: Long,
    val name: String,
    val companyId: Long,
    val employeeId: String
)

data class Pet(
    val id: Long,
    val name: String,
    val type: String,
    val companyId: Long,
    val dateOfArrival: LocalDate
)

enum class PetTypes{
    Dog,
    Cat
}