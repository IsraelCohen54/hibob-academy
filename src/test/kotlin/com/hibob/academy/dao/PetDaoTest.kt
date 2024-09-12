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

        petDao.insertPet(name = "Rex", type = PetType.Dog.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))
        petDao.insertPet(name = "Rexi", type = PetType.Dog.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2))

        val fetchedPets = petDao.getPetsByType(PetType.Dog, companyId)

        assertEquals(2, fetchedPets.size)
    }

    @Test
    fun `get pets by type test`() {

        petDao.insertPet(name = "Rex", type = PetType.Dog.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,1,1))
        petDao.insertPet(name = "Whiskers", type = PetType.Cat.toString(), companyId = companyId, dateOfArrival = LocalDate.of(2024,2,2))

        val fetchedDogs = petDao.getPetsByType(PetType.Dog, companyId)
        val fetchedCats = petDao.getPetsByType(PetType.Cat, companyId)

        assertEquals(1, fetchedDogs.size)
        assertEquals(1, fetchedCats.size)
    }

    @Test
    fun `get pets by type test without any pets`() {
        val emptyList: List<Pet> = emptyList()

        val fetchedPets = petDao.getPetsByType(PetType.Dog, companyId)

        assertEquals(emptyList, fetchedPets)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}
