package com.hibob.academy.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import jakarta.inject.Inject

@Component
class OwnerDao @Inject constructor(private val sql: DSLContext) {

    private val ownerTable = OwnerTable.ownerInstance

    private val ownerMapper = RecordMapper<Record, Owner> { record ->
        Owner(
            id = record[ownerTable.id],
            name = record[ownerTable.name],
            companyId = record[ownerTable.companyId],
            employeeId = record[ownerTable.employeeId]
        )
    }

    fun insertOwner(name: String, companyId: Long, employeeId: String) {
        sql.insertInto(ownerTable)
            .set(ownerTable.name, name)
            .set(ownerTable.companyId, companyId)
            .set(ownerTable.employeeId, employeeId)
            .onConflict(ownerTable.companyId, ownerTable.employeeId)
            .doNothing()
            .execute()
    }

    fun getOwners(companyId: Long): List<Owner?> =
        sql.selectFrom(ownerTable)
            .where(ownerTable.companyId.eq(companyId))
            .fetch(ownerMapper)
}