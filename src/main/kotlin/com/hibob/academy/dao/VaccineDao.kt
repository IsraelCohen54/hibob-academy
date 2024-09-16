package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class VaccineDao @Inject constructor(private val sql: DSLContext) {

    private val vaccineTable = VaccineTable.vaccineInstance

    private val vaccineMapper = RecordMapper<Record, Vaccine> { record ->
        Vaccine(
            id = record[vaccineTable.id],
            name = record[vaccineTable.name]
        )
    }

    /* // maybe I would like to add after
    fun createVaccine(id: Long, name: String) {
        sql.insertInto(vaccineTable)
            .set(vaccineTable.id, id)
            .set(vaccineTable.name, name)
            .execute()
    }

    fun readVaccine(name: String): Vaccine? =
        sql.selectFrom(vaccineTable)
            .where(vaccineTable.name.eq(name))
            .fetchOne { record ->
                Vaccine(
                    id = record[vaccineTable.id],
                    name = record[vaccineTable.name]
                )
            }

     */
}
