package com.hibob.academy.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PetDao(private val sql: DSLContext) {

    private val petTable = PetTable.petInstance
    private val ownerTable = OwnerTable.ownerInstance

    private val petTableMapper = RecordMapper<Record, Pet> { record ->
        Pet(
            id = record[petTable.id],
            name = record[petTable.name],
            type = PetType.fromString(record[(petTable.type)]),
            companyId = record[petTable.companyId],
            dateOfArrival = record[petTable.dateOfArrival],
            ownerId = record[petTable.ownerId]
        )
    }

    fun insertPet(name: String, type: PetType, companyId: Long, dateOfArrival: LocalDate, ownerId: Long? = null) {
        sql.insertInto(petTable)
            .set(petTable.name, name)
            .set(petTable.type, type.toString())
            .set(petTable.companyId, companyId)
            .set(petTable.dateOfArrival, dateOfArrival)
            .set(petTable.ownerId, ownerId)
            .execute()
    }

    fun getPetsByType(type: PetType, companyId: Long): List<Pet> {
        return sql.select()
            .from(petTable)
            .where(petTable.type.eq(type.toString()))
            .and(petTable.companyId.eq(companyId))
            .fetch(petTableMapper)
    }

    fun getPetId(name: String, type: PetType, companyId: Long, dateOfArrival: LocalDate): Long? {
        return sql.select(petTable.id)
            .from(petTable)
            .where(
                petTable.name.eq(name)
                    .and(petTable.companyId.eq(companyId))
                    .and(petTable.type.eq(type.toString()))
                    .and(petTable.dateOfArrival.eq(dateOfArrival))
            )
            .limit(1)
            .fetchOne(petTable.id)
    }

    fun adopt(petId: Long, ownerId: Long, companyId: Long) {
        sql.update(petTable)
            .set(petTable.ownerId, ownerId)
            .where(
                petTable.id.eq(petId)
                    .and(petTable.companyId.eq(companyId))
            )
            .execute()
    }

    fun getPetOwnerId(petId: Long, companyId: Long): Long? {
        return sql.select(petTable.ownerId)
            .from(petTable)
            .where(
                petTable.id.eq(petId)
                    .and(petTable.companyId.eq(companyId))
            )
            .fetchOne(petTable.ownerId)
    }

    fun getPetOwner(petId: Long, companyId: Long): Owner? {
        return sql.select()
            .from(petTable)
            .join(ownerTable)
            .on(petTable.ownerId.eq(ownerTable.id))
            .where(
                petTable.id.eq(petId)
                    .and(petTable.companyId.eq(companyId))
            )
            .fetchOne { record ->
                Owner(
                    id = record[ownerTable.id],
                    name = record[ownerTable.name],
                    employeeId = record[ownerTable.employeeId],
                    companyId = record[ownerTable.companyId]
                )
            }
    }

    fun getPetsByOwnerId(ownerId: Long, companyId: Long): List<Pet> {
        return sql.select()
            .from(petTable)
            .where(
                petTable.ownerId.eq(ownerId)
                    .and(petTable.companyId.eq(companyId))
            )
            .fetch(petTableMapper)
    }

    fun countPetsByType(companyId: Long): Map<PetType, Int> {
        val count = DSL.count(petTable)
        return sql.select(petTable.type, count)
            .from(petTable)
            .where(petTable.companyId.eq(companyId))
            .groupBy(petTable.type)
            .fetch()
            .intoMap(
                { record -> PetType.valueOf(record[petTable.type].toString()) },
                { record -> record[count] })
    }

}
