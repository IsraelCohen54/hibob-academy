package com.hibob.academy.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import jakarta.inject.Inject
import org.jooq.Record
import org.jooq.RecordMapper

// todo dao create update + tests pr 1 + test the fetch from yesterday)
//

@Component
class PetDao @Inject constructor(private val sql: DSLContext) {

    private val petTableInstance = PetTable.petInstance

    private val petTableMapper = RecordMapper<Record, Pet> { record ->
        Pet(
            id = record[petTableInstance.id],
            name = record[petTableInstance.name],
            type = record[petTableInstance.type],
            companyId = record[petTableInstance.companyId],
            dateOfArrival = record[petTableInstance.dateOfArrival]//.substring(0,3)
        )
    }

    fun createPet(name: String, type: String, companyId: Long, dateOfArrival: java.sql.Date) {
        sql.insertInto(petTableInstance)
            .columns(petTableInstance.name, petTableInstance.type, petTableInstance.companyId, petTableInstance.dateOfArrival)
            .values(name, type, companyId, dateOfArrival)
            .execute()
    }

    fun readPet(id: Long): Pet? =
        sql.selectFrom(petTableInstance)
            .where(petTableInstance.id.eq(id))
            .fetchOne(petTableMapper) // Use the correct mapper here

    fun getPetsByType(type: PetTypes): List<Pet> {
        return sql.select()
            .from(petTableInstance)
            .where(petTableInstance.type.eq(type.toString()))
            .fetch(petTableMapper)
    }

    enum class PetTypes{
        Dog,
        Cat
    }

    data class Pet(
        val id: Long,
        val name: String,
        val type: String,
        val companyId: Long,
        val dateOfArrival: java.sql.Date
    )
}

