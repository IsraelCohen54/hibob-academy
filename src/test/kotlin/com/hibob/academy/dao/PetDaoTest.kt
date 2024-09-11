package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.random.Random
import java.time.LocalDate

@BobDbTest
class PetDaoTest @Inject constructor(private val sql: DSLContext) {

    private val table = PetTable.petInstance
    private val companyId = Random.nextLong()
    private val petDao = PetDao(sql)

    @Test
    fun `insert pet test`() {
        val pet = Pet(1, name = "Rex", type = PetTypes.Dog.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))
        petDao.createPet(pet.name, pet.type, pet.companyId, pet.dateOfArrival)

        val fetchedPets = petDao.getPetsByType(PetTypes.Dog, companyId)

        assertEquals(listOf(pet), fetchedPets)
    }

    @Test
    fun `get pets by type test`() {

        val pet1 = Pet(1, name = "Rex", type = PetTypes.Dog.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,1,1))
        val pet2 = Pet(2, name = "Whiskers", type = PetTypes.Cat.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,0,2))
        petDao.createPet(pet1.name, pet1.type, pet1.companyId, pet1.dateOfArrival)
        petDao.createPet(pet2.name, pet2.type, pet2.companyId, pet2.dateOfArrival)

        val fetchedDogs = petDao.getPetsByType(PetTypes.Dog, companyId)
        val fetchedCats = petDao.getPetsByType(PetTypes.Cat, companyId)

        assertEquals(listOf(pet1), fetchedDogs)
        assertEquals(listOf(pet2), fetchedCats)
    }

    @Test
    fun `get pets by type test without any pets`() {
        val emptyList: List<Pet> = emptyList()

        val fetchedPets = petDao.getPetsByType(PetTypes.Dog, companyId)

        assertEquals(emptyList, fetchedPets)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}
