package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.random.Random
import java.sql.Date

@BobDbTest
class PetDaoTest @Inject constructor(private val sql: DSLContext) {

    private val table = PetTable.petInstance
    private val companyId = Random.nextLong()
    private val petDao = PetDao(sql)

    @Test
    fun `insert pet test`() {
        val pet = PetDao.Pet(name = "Rex", type = PetDao.PetTypes.Dog.toString(), companyId = companyId, dateOfArrival = Date.valueOf("2024-01-01"))
        petDao.createPet(pet.name, pet.type, pet.companyId, pet.dateOfArrival)

        val fetchedPets = petDao.getPetsByType(PetDao.PetTypes.Dog, companyId)

        assertEquals(listOf(pet), fetchedPets)
    }

    @Test
    fun `get pets by type test`() {

        val pet1 = PetDao.Pet(name = "Rex", type = PetDao.PetTypes.Dog.toString(), companyId = companyId, dateOfArrival = Date.valueOf("2024-01-01"))
        val pet2 = PetDao.Pet(name = "Whiskers", type = PetDao.PetTypes.Cat.toString(), companyId = companyId, dateOfArrival = Date.valueOf("2024-01-02"))
        petDao.createPet(pet1.name, pet1.type, pet1.companyId, pet1.dateOfArrival)
        petDao.createPet(pet2.name, pet2.type, pet2.companyId, pet2.dateOfArrival)

        val fetchedDogs = petDao.getPetsByType(PetDao.PetTypes.Dog, companyId)
        val fetchedCats = petDao.getPetsByType(PetDao.PetTypes.Cat, companyId)

        assertEquals(listOf(pet1), fetchedDogs)
        assertEquals(listOf(pet2), fetchedCats)
    }

    @Test
    fun `get pets by type test without any pets`() {
        val emptyList: List<PetDao.Pet> = emptyList()

        val fetchedPets = petDao.getPetsByType(PetDao.PetTypes.Dog, companyId)

        assertEquals(emptyList, fetchedPets)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}
