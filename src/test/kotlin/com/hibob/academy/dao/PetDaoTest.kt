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
class PetDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val petTable = PetTable.petInstance
    private val companyId = Random.nextLong()
    private val petDao = PetDao(sql)

    // Test fields
    private val ownerId1 = 101L
    private val ownerId2 = 102L
    private val petWithoutId1 = PetWithoutId(
        name = "Rex",
        type = PetType.DOG,
        companyId = companyId,
        dateOfArrival = LocalDate.of(2024, 1, 1),
        ownerId1
    )
    private val petWithoutId2 = PetWithoutId(
        name = "Whiskers",
        type = PetType.CAT,
        companyId = companyId,
        dateOfArrival = LocalDate.of(2024, 2, 2),
        ownerId2
    )
    private val petWithoutId3 = PetWithoutId(
        name = "Rexi",
        type = PetType.DOG,
        companyId = companyId,
        dateOfArrival = LocalDate.of(2024, 1, 2),
        ownerId1
    )
    private val ownerlessPetWithoutId3 = PetWithoutId(
        name = "Rexi",
        type = PetType.DOG,
        companyId = companyId,
        dateOfArrival = LocalDate.of(2024, 1, 2),
        null
    )

    @Test
    fun `insert pet test`() {
        val petId = petDao.insertPet(petWithoutId1)

        val insertedPet = petDao.getPetsByType(PetType.DOG, companyId)

        petId?.let {
            val originPet = Pet(petId, petWithoutId1)

            assertEquals(originPet, insertedPet[0])
        }
    }

    @Test
    fun `get pets by type test`() {

        val dog1Id = petDao.insertPet(petWithoutId1)
        val cat2Id = petDao.insertPet(petWithoutId2)

        val fetchedDogs = petDao.getPetsByType(PetType.DOG, companyId)
        val fetchedCats = petDao.getPetsByType(PetType.CAT, companyId)

        dog1Id?.let {
            val rexDog = Pet(dog1Id, petWithoutId1)
            cat2Id?.let {
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

        val petOneId = petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId3) // misleading test
        petOneId?.let {
            val pet = petDao.getPetById(petOneId, companyId)
            assertEquals(pet, Pet(petOneId, petWithoutId1))
        }
    }

    @Test
    fun `getPetById should return the correct pet`() {

        val petId = petDao.insertPet(petWithoutId1)
        petId?.let {
            val fetchedPet = petDao.getPetById(petId, companyId)

            assertNotNull(fetchedPet, "Fetched pet should not be null")
            assertEquals(Pet(petId, petWithoutId1), fetchedPet, "The fetched pet should match the expected pet")
        }
    }

    @Test
    fun `getPetById should return null for non-existent pet`() {

        val nonExistentPetId = 999L

        val fetchedPet = petDao.getPetById(nonExistentPetId, companyId)

        assertEquals(null, fetchedPet, "Fetched pet should be null for non-existent ID")
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
        val petWithoutId4 = PetWithoutId(
            name = "Rexi",
            type = PetType.DOG,
            companyId = companyId,
            dateOfArrival = LocalDate.of(2024, 1, 2),
            ownerId
        )
        val petId = petDao.insertPet(petWithoutId4)

        petId?.let {
            val petOwner = petDao.getPetOwner(petId, companyId)
            ownerId?.let {
                assertEquals(petOwner, ownerDao.getOwnerById(ownerId, companyId))
            }
        }
    }

    @Test
    fun `test getPetOwnerId returns null for pet without owner`() {
        val petId = petDao.insertPet(ownerlessPetWithoutId3)

        petId?.let {
            val fetchedOwnerId = petDao.getPetOwnerId(petId, companyId)
            assertEquals(null, fetchedOwnerId, "The retrieved owner ID should be null if no owner has been set")
        }
    }

    @Test
    fun `test getPetsByOwnerId returns list of pets`() {

        val id1 = petDao.insertPet(petWithoutId1)
        petDao.insertPet(petWithoutId2)
        val id3 = petDao.insertPet(petWithoutId3)

        val ownerPets = petDao.getPetsByOwnerId(ownerId1, companyId)

        id1?.let {
            id3?.let {
                val expectedOwnerPets = listOf(
                    Pet(id1, petWithoutId1),
                    Pet(id3, petWithoutId3),
                )
                assertEquals(expectedOwnerPets, ownerPets)
            }
        }
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

        val petId1 = petDao.insertPet(petWithoutId1)
        val petId2 = petDao.insertPet(petWithoutId2)
        val petId3 = petDao.insertPet(petWithoutId3)

        petId1?.let {
            petId3?.let {
                val petsToAdopt = listOf(petId1, petId3)


                petDao.adoptMultiplePets(companyId, newOwnerId, petsToAdopt)

                assertEquals(newOwnerId, petDao.getPetOwnerId(petId1, companyId))
                assertEquals(newOwnerId, petDao.getPetOwnerId(petId3, companyId))
                petId2?.let {
                    assertEquals(petWithoutId2.ownerId, petDao.getPetOwnerId(petId2, companyId))
                }
            }
        }
    }


    @Test
    fun `addMultiplePets should add or update pets correctly`() {

        val petId1 = petDao.insertPet(petWithoutId1)
        val petId2 = petDao.insertPet(petWithoutId3)
        petId1?.let {
            petId2?.let {
                val pets = listOf(
                    Pet(id = petId1, petWithoutId1),
                    Pet(id = petId2, petWithoutId3)
                )

                petDao.addMultiplePets(companyId = companyId, pets = pets)

                val fetchedPet1 = petDao.getPetById(petId1, companyId)
                val fetchedPet2 = petDao.getPetById(petId2, companyId)

                assertEquals(Pet(id = petId1, petWithoutId1), fetchedPet1, "Pet with ID $petId1 should match")
                assertEquals(Pet(id = petId2, petWithoutId3), fetchedPet2, "Pet with ID $petId2 should match")
            }
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
