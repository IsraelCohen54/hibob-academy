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

        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))
        petDao.insertPet(name = "Rexi", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2))

        val fetchedPets = petDao.getPetsByType(PetType.DOG, companyId)

        assertEquals(2, fetchedPets.size)
    }

    @Test
    fun `get pets by type test`() {

        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1,1))
        petDao.insertPet(name = "Whiskers", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2024,2,2))

        val fetchedDogs = petDao.getPetsByType(PetType.DOG, companyId)
        val fetchedCats = petDao.getPetsByType(PetType.CAT, companyId)

        val dog1Id = petDao.getPetId("Rex", PetType.DOG, companyId, LocalDate.of(2024,1,1))
        val cat2Id = petDao.getPetId("Whiskers", PetType.CAT, companyId, LocalDate.of(2024,2,2))

        dog1Id?.let {
            val rexDog = Pet(dog1Id, "Rex", PetType.DOG, companyId, LocalDate.of(2024,1,1))
            cat2Id?.let{
                val whiskersCat = Pet(cat2Id, "Whiskers", PetType.CAT, companyId, LocalDate.of(2024,2,2))

                assertEquals(rexDog, fetchedDogs[0])
                assertEquals(whiskersCat, fetchedCats[0])
            }
        }
    }

    @Test
    fun `get pets by type test without any pets`() {
        val emptyList: List<Pet> = emptyList()

        val fetchedPets = petDao.getPetsByType(PetType.DOG, companyId)

        assertEquals(emptyList, fetchedPets)
    }

    @Test
    fun `get Pet Id`() {

        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))
        petDao.insertPet(name = "Rexi", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2))

        val petId: Long? = petDao.getPetId(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))
        petId?.let {
            val pet = Pet(id = petId, name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))

            // Fetch pets by type
            val pets = petDao.getPetsByType(PetType.DOG, companyId)

            assertEquals(pet, pets.iterator().asSequence().find { it.id == petId })
        }
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}
