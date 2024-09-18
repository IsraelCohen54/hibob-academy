package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Autowired constructor (private val sql: DSLContext) {

    private val petTable = PetTable.petInstance
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

    @Test
    fun `test getPetsByOwnerId returns list of pets` () {
        petDao.insertPet(name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = 101)
        petDao.insertPet(name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = 101)
        petDao.insertPet(name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = 101)
        petDao.insertPet(name = "Rex4", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = 100)

        val ownerPets = petDao.getPetsByOwnerId(101, companyId)

        val expectedOwnerPets = listOf(
            Pet(id = petDao.getPetId(name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6))!!, name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = 101),
            Pet(id = petDao.getPetId(name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5))!!, name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = 101),
            Pet(id = petDao.getPetId(name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5))!!, name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = 101)
        )
        assertEquals(expectedOwnerPets, ownerPets)
    }

    @Test
    fun `test countPetsByType returns correct pet counts by type`() {
        // Insert some pets into the database
        petDao.insertPet(name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = 101)
        petDao.insertPet(name = "Fluffy", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = 101)
        petDao.insertPet(name = "Max", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = 102)
        petDao.insertPet(name = "Buddy", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = 100)

        val petCountByType = petDao.countPetsByType(companyId)

        val expectedPetCountByType = mapOf(
            PetType.DOG to 3,
            PetType.CAT to 1
        )

        assertEquals(expectedPetCountByType, petCountByType)
    }

    @Test
    fun `test adoptMultiplePets updates owner for multiple pets`() {
        val oldOwnerId = 101L
        val newOwnerId = 202L

        petDao.insertPet("Rex1", PetType.DOG, companyId, LocalDate.of(2024, 1, 6), oldOwnerId)
        petDao.insertPet("Rex2", PetType.CAT, companyId, LocalDate.of(2024, 2, 7), oldOwnerId)
        petDao.insertPet("Rex3", PetType.CAT, companyId, LocalDate.of(2024, 3, 8), oldOwnerId)

        val petId1
        val petId2
        val petId3

        val petsToAdopt = listOf(petId1, petId2)

        // Call adoptMultiplePets to update ownerId for the selected pets
        petDao.adoptMultiplePets(companyId, newOwnerId, petsToAdopt)

        // Fetch the updated pets from the database
        val updatedPet1 = petDao.getPetById(petId1)
        val updatedPet2 = petDao.getPetById(petId2)
        val notUpdatedPet = petDao.getPetById(petId3)

        // Assert that the ownerId for the selected pets was updated
        assertEquals(newOwnerId, updatedPet1?.ownerId)
        assertEquals(newOwnerId, updatedPet2?.ownerId)

        // Assert that the ownerId for the other pet remains unchanged
        assertEquals(oldOwnerId, notUpdatedPet?.ownerId)
    }


    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(petTable).where(petTable.companyId.eq(companyId)).execute()

        val ownerTable = OwnerTable.ownerInstance
        sql.deleteFrom(ownerTable).where(ownerTable.companyId.eq(companyId)).execute()
    }
}
