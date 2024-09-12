package com.hibob.academy.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import jakarta.inject.Inject
import org.jooq.Record
import org.jooq.RecordMapper
import java.time.LocalDate

// todo dao create update + tests pr 1 + test the fetch from yesterday)
//

@Component
class PetDao @Inject constructor(private val sql: DSLContext) {

    private val petTable = PetTable.petInstance

    private val petTableMapper = RecordMapper<Record, Pet> { record ->
        Pet(
            id = record[petTable.id],
            name = record[petTable.name],
            type = record[petTable.type],
            companyId = record[petTable.companyId],
            dateOfArrival = record[petTable.dateOfArrival]
        )
    }

    fun insertPet(name: String, type: String, companyId: Long, dateOfArrival: LocalDate) {
        sql.insertInto(petTable)
            .set(petTable.name, name)
            .set(petTable.type, type)
            .set(petTable.companyId, companyId)
            .set(petTable.dateOfArrival, dateOfArrival)
            .execute()
    }

    fun getPetsByType(type: PetTypes, companyId: Long): List<Pet> {
        return sql.select()
            .from(petTable)
            .where(petTable.type.eq(type.toString()))
            .and( petTable.companyId.eq(companyId))
            .fetch(petTableMapper)
    }
}

