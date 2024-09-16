package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class VaccineToPetDao @Inject constructor(private val sql: DSLContext) {

    private val vaccineToPetTable = VaccineToPetTable.vaccineToPetInstance

    private val vaccineToPetMapper = RecordMapper<Record, VaccineToPet> { record ->
        VaccineToPet(
            id = record[vaccineToPetTable.id],
            name = record[vaccineToPetTable.name],
            vaccinationDate = record[vaccineToPetTable.vaccinationDate],
            petId = record[vaccineToPetTable.petId]
        )
    }
}
