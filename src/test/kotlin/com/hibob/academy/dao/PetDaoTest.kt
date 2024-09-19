package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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

    // Test fields
    private val ownerId1 = 101L
    private val ownerId2 = 102L
    private val petWithoutId1 = PetWithoutId (name = "Rex", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 1), ownerId1)
    private val petWithoutId2 = PetWithoutId (name = "Whiskers", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2024,2,2),ownerId2)
    private val petWithoutId3 = PetWithoutId(name = "Rexi", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2), ownerId1)
    private val ownerlessPetWithoutId3 = PetWithoutId(name = "Rexi", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2), null)

    @Test
    fun `insert pet test`() {

        petDao.insertPet(petWithoutId1)

        val insertedPet = petDao.getPetsByType(PetType.DOG, companyId)
        val petId = petDao.getPetId(petWithoutId1)!!
        val originPet = Pet(petId, petWithoutId1)
        assertEquals(originPet, insertedPet[0])
    }

    @Test
    fun `get pets by type test`() {

        petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId2)

        val fetchedDogs = petDao.getPetsByType(PetType.DOG, companyId)
        val fetchedCats = petDao.getPetsByType(PetType.CAT, companyId)

        val dog1Id = petDao.getPetId(petWithoutId1)
        val cat2Id = petDao.getPetId(petWithoutId2)

        dog1Id?.let {
            val rexDog = Pet(dog1Id, petWithoutId1)
            cat2Id?.let{
                val whiskersCat = Pet(cat2Id, petWithoutId2)

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

        petDao.insertPet(petWithoutId1)

        petDao.insertPet(petWithoutId3)

        val petId: Long? = petDao.getPetId(petWithoutId1)
        petId?.let {
            val pet = Pet(id = petId, petWithoutId1)

            val pets = petDao.getPetsByType(PetType.DOG, companyId)

            assertEquals(pet, pets.iterator().asSequence().find { it.id == petId })
        }
    }

    @Test
    fun `test adopt updates ownerId`() {
        petDao.insertPet(petWithoutId1)

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
        val petWithoutId4 = PetWithoutId(name = "Rexi", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024,1, 2), ownerId)
        petDao.insertPet(petWithoutId4)

        val petId = petDao.getPetId(petWithoutId1)
        petId?.let{
            val petOwner = petDao.getPetOwner(petId, companyId)
            ownerId?.let {
                assertEquals(petOwner, ownerDao.getOwnerById(ownerId, companyId))
            }
        }
    }

    @Test
    fun `test getPetOwnerId returns null for pet without owner`() {
        petDao.insertPet(ownerlessPetWithoutId3)
        val petId = petDao.getPetId(ownerlessPetWithoutId3)

        petId?.let {
            val fetchedOwnerId = petDao.getPetOwnerId(petId, companyId)
            assertEquals(null, fetchedOwnerId, "The retrieved owner ID should be null if no owner has been set")
        }
    }

    @Test
    fun `test getPetsByOwnerId returns list of pets` () {

        petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId2)
        petDao.insertPet(petWithoutId3)

        val ownerPets = petDao.getPetsByOwnerId(ownerId1, companyId)

        val expectedOwnerPets = listOf(
            Pet(id = petDao.getPetId(petWithoutId1)!!, petWithoutId1),
            Pet(id = petDao.getPetId(petWithoutId3)!!, petWithoutId3),
        )
        assertEquals(expectedOwnerPets, ownerPets)
    }

    @Test
    fun `test countPetsByType returns correct pet counts by type`() {

        petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId2)
        petDao.insertPet(petWithoutId3)

        val petCountByType = petDao.countPetsByType(companyId)

        val expectedPetCountByType = mapOf(
            PetType.DOG to 2,
            PetType.CAT to 1
        )

        assertEquals(expectedPetCountByType, petCountByType)
    }

    @Test
    fun `test adoptMultiplePets updates owner for multiple pets`() {
        val newOwnerId = 202L

        petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId2)
        petDao.insertPet(petWithoutId3)

        val petId1 = petDao.getPetId(petWithoutId1)!!
        val petId2 = petDao.getPetId(petWithoutId2)!!
        val petId3 = petDao.getPetId(petWithoutId3)!!

        val petsToAdopt = listOf(petId1, petId3)

        petDao.adoptMultiplePets(companyId, newOwnerId, petsToAdopt)

        assertEquals(newOwnerId, petDao.getPetOwnerId(petId1, companyId))
        assertEquals(newOwnerId, petDao.getPetOwnerId(petId3, companyId))

        assertEquals(petWithoutId2.ownerId, petDao.getPetOwnerId(petId2, companyId))
    }


    @Test
    fun `addMultiplePets should add or update pets correctly - validation using the conflict implemented for date`() {

        petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId3)

        val petId1 = petDao.getPetId(petWithoutId1)!!
        val petId2 = petDao.getPetId(petWithoutId3)!!

        val pets = listOf(
            Pet(id = petId1, petWithoutId1),
            Pet(id = petId2, petWithoutId3))

        petDao.addMultiplePets(companyId = companyId, pets = pets)

        val fetchedPets = sql.selectFrom(petTable)
            .where(petTable.id.`in`(petId1, petId2))
            .fetchInto(Pet::class.java)

        val fetchedPetsById = fetchedPets.associateBy { it.id }

        assertEquals(pets.size, fetchedPets.size, "The number of records should match")

        pets.forEach { expectedPet ->
            val fetchedPet = fetchedPetsById[expectedPet.id]
            assertNotNull(fetchedPet, "Pet with ID ${expectedPet.id} should exist")
            assertEquals(expectedPet, fetchedPet, "Pet with ID ${expectedPet.id} should match")
        }
    }


    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(petTable).where(petTable.companyId.eq(companyId)).execute()

        val ownerTable = OwnerTable.ownerInstance
        sql.deleteFrom(ownerTable).where(ownerTable.companyId.eq(companyId)).execute()
    }
}
