package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Inject constructor(private val sql: DSLContext) {

    private val table = PetTable.petInstance
    private val companyId = Random.nextLong()
    private val petDao = PetDao(sql)

    @Test
    fun `insert pet test`() {

        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))

        val insertedPet = petDao.getPetsByType(PetType.DOG, companyId)
        val originPet = Pet(petDao.getPetId(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1))!!, name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1), null)
        assertEquals(originPet, insertedPet[0])
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
            val rexDog = Pet(dog1Id, "Rex", PetType.DOG, companyId, LocalDate.of(2024,1,1), null)
            cat2Id?.let{
                val whiskersCat = Pet(cat2Id, "Whiskers", PetType.CAT, companyId, LocalDate.of(2024,2,2), null)

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
            val pet = Pet(id = petId, name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1), null)

            // Fetch pets by type
            val pets = petDao.getPetsByType(PetType.DOG, companyId)

            assertEquals(pet, pets.iterator().asSequence().find { it.id == petId })
        }
    }

    @Test
    fun `test adopt updates ownerId`() {
        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 1))

        val petId = petDao.getPetsByType(PetType.DOG, companyId).first().id

        val ownerId = 100L
        petDao.adopt(petId, ownerId, companyId)

        val fetchedOwnerId = petDao.getPetOwnerId(petId, companyId)
        assertEquals(ownerId, fetchedOwnerId, "The owner ID should match the expected owner ID after adoption")
    }

    @Test
    fun `test getPetOwner returns correct owner`() {

        val ownerDao = OwnerDao(sql)
        ownerDao.insertOwner("Bob1", companyId, "123")

        val ownerId = ownerDao.getOwnerId(companyId, "123")
        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId,
            dateOfArrival = LocalDate.of(2024, 1, 1), ownerId = ownerId)

        val petId = petDao.getPetId(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 1))
        petId?.let{
            val petOwner = petDao.getPetOwner(petId, companyId)
            ownerId?.let {
                assertEquals(petOwner, ownerDao.getOwnerById(ownerId, companyId))
            }
        }
    }

    @Test
    fun `test getPetOwnerId returns null for pet without owner`() {
        petDao.insertPet(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 1))
        val petId = petDao.getPetId(name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 1))

        petId?.let {
            val fetchedOwnerId = petDao.getPetOwnerId(petId, companyId)
            assertEquals(null, fetchedOwnerId, "The retrieved owner ID should be null if no owner has been set")
        }
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        val ownerTable = PetTable.petInstance
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
        sql.deleteFrom(ownerTable).where(table.companyId.eq(companyId)).execute()
    }
}
