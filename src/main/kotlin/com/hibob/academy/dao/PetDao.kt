package com.hibob.academy.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL
import org.springframework.stereotype.Component

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

    fun insertPet(petWithoutId: PetWithoutId): Long {
        return sql.insertInto(petTable)
            .set(petTable.name, petWithoutId.name)
            .set(petTable.type, petWithoutId.type.toString())
            .set(petTable.companyId, petWithoutId.companyId)
            .set(petTable.dateOfArrival, petWithoutId.dateOfArrival)
            .set(petTable.ownerId, petWithoutId.ownerId)
            .returning(petTable.id)
            .fetchOne()
            ?.get(petTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated ID.")
    }

    fun getPetsByType(type: PetType, companyId: Long): List<Pet> {
        return sql.select()
            .from(petTable)
            .where(petTable.type.eq(type.toString()))
            .and(petTable.companyId.eq(companyId))
            .fetch(petTableMapper)
    }

    fun getPetById(id: Long, companyId: Long): Pet? {
        return sql.select()
            .from(petTable)
            .where(
                petTable.id.eq(id)
                    .and(petTable.companyId.eq(companyId))
            )
            .fetchOne(petTableMapper)
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

    fun adoptMultiplePets(companyId: Long, ownerId: Long, petsId: List<Long>) {
        sql.update(petTable)
            .set(petTable.ownerId, ownerId)
            .where(
                petTable.companyId.eq(companyId)
                    .and(petTable.id.`in`(petsId))
            )
            .execute()
    }

    fun addMultiplePets(companyId: Long, pets: List<Pet>) {
        val insert = sql.insertInto(petTable)
            .columns(petTable.name, petTable.type, petTable.companyId, petTable.dateOfArrival, petTable.ownerId)
            .values(
                DSL.param(petTable.name),
                DSL.param(petTable.type),
                DSL.param(petTable.companyId),
                DSL.param(petTable.dateOfArrival),
                DSL.param(petTable.ownerId)
            )

        val batch = sql.batch(insert)

        pets.forEach { pet ->
            batch.bind(pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId)
        }

        batch.execute()
    }
}
