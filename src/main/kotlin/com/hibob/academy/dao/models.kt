package com.hibob.academy.dao

import com.hibob.nullability_exercises.nullSafeToUpper
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
    val type: PetType,
    val companyId: Long,
    val dateOfArrival: LocalDate,
    val ownerId: Long?
)

enum class PetType{
    DOG,
    CAT;

    companion object {
        // convert a String to a PetType
        fun fromString(value: String): PetType {
            return PetType.valueOf(value.nullSafeToUpper())
        }
    }
}