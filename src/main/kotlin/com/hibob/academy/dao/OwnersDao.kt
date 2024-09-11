package com.hibob.academy.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import jakarta.inject.Inject

@Component
class OwnerDao @Inject constructor(private val sql: DSLContext) {

    private val ownerTableInstance = OwnerTable.ownerInstance

    private val ownerMapper = RecordMapper<Record, Owner> { record ->
        Owner(
            //id = record[ownerTableInstance.id],
            name = record[ownerTableInstance.name],
            companyId = record[ownerTableInstance.companyId],
            employeeId = record[ownerTableInstance.employeeId]
        )
    }

    fun insertOwner(name: String, companyId: Long, employeeId: String) {
        sql.insertInto(ownerTableInstance)
            .columns(ownerTableInstance.name, ownerTableInstance.companyId, ownerTableInstance.employeeId)
            .values(name, companyId, employeeId)
            //.onConflict(ownerTableInstance.name, ownerTableInstance.companyId, ownerTableInstance.employeeId)
            //.doNothing()
            .execute()
    }

    /*
    fun readOwner(id: Long): Owner? =
        sql.selectFrom(ownerTableInstance)
            .where(ownerTableInstance.id.eq(id))
            .fetchOne(ownerMapper)
    */

    fun getOwners(companyId: Long): List<Owner?> =
        sql.selectFrom(ownerTableInstance)
            .where(ownerTableInstance.companyId.eq(companyId))
            .fetch(ownerMapper)

    data class Owner(
        //val id: Long,
        val name: String,
        val companyId: Long,
        val employeeId: String
    )
}
