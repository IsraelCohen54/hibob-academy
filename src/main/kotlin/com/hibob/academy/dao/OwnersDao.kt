package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class OwnerDao @Inject constructor(private val sql: DSLContext) {

    private val ownerTable = OwnerTable.ownerInstance

    val ownerMapper = RecordMapper<Record, Owner> { record ->
        Owner(
            id = record[ownerTable.id],
            name = record[ownerTable.name],
            companyId = record[ownerTable.companyId],
            employeeId = record[ownerTable.employeeId]
        )
    }

    fun insertOwner(name: String, companyId: Long, employeeId: String): Long {
        return sql.insertInto(ownerTable)
            .set(ownerTable.name, name)
            .set(ownerTable.companyId, companyId)
            .set(ownerTable.employeeId, employeeId)
            .onConflict(ownerTable.companyId, ownerTable.employeeId)
            .doNothing()
            .returning(ownerTable.id)
            .fetchOne()
            ?.get(ownerTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated ID.")
    }

    fun getOwners(companyId: Long): List<Owner?> =
        sql.selectFrom(ownerTable)
            .where(ownerTable.companyId.eq(companyId))
            .fetch(ownerMapper)

    fun getOwnerId(companyId: Long, employeeId: String): Long? {
        return sql.selectFrom(ownerTable)
            .where(
                ownerTable.companyId.eq(companyId)
                    .and(ownerTable.employeeId.eq(employeeId))
            )
            .fetchOne(ownerTable.id)
    }

    fun getOwnerById(id: Long, companyId: Long): Owner? {
        return sql.selectFrom(ownerTable) // Specify the table to select from
            .where(
                ownerTable.companyId.eq(companyId)
                    .and(ownerTable.id.eq(id)) // Convert `id` to Long if necessary
            )
            .fetchOne(ownerMapper)
    }
}