package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import com.hibob.academy.dao.PetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.random.Random

class PetServiceTest {

    private val petDao: PetDao = mock {}
    private val petService = PetService(petDao)
    private val companyId = Random.nextLong()

    @Test
    fun `adoptPet should throw IllegalStateException if pet already has an owner`() {
        val petId = 1L
        val ownerId = 2L
        val fakeAlreadyExistedOwnerId = 3L

        whenever(petDao.getPetOwnerId(petId, companyId)).thenReturn(fakeAlreadyExistedOwnerId)

        val exception = assertThrows<IllegalStateException> {
            petService.adoptPet(petId, ownerId, companyId)
        }
        assertEquals("An owner for the pet already exists", exception.message)

        verify(petDao, never()).adopt(any(), any(), any())
    }

    @Test
    fun `adoptPet should call adopt on PetDao if pet does not have an owner`() {
        val petId = 1L
        val ownerId = 2L

        whenever(petDao.getPetOwnerId(petId, companyId)).thenReturn(null)

        petService.adoptPet(petId, ownerId, companyId)

        verify(petDao).adopt(eq(petId), eq(ownerId), eq(companyId))
    }

    @Test
    fun `getOwnerByPetId should return owner if present`() {
        val petId = 1L
        val expectedOwner = Owner(id = 2L, name = "John Doe", companyId = companyId, employeeId = "E123")

        whenever(petDao.getPetOwner(petId, companyId)).thenReturn(expectedOwner)

        val result = petService.getOwnerByPetId(petId, companyId)

        assertEquals(expectedOwner, result)
    }

    @Test
    fun `getOwnerByPetId should throw IllegalStateException when owner does not exist`() {

        val petId = 1L
        whenever(petDao.getPetOwner(petId, companyId)).thenReturn(null)

        val exception = assertThrows<IllegalStateException> {
            petService.getOwnerByPetId(petId, companyId)
        }
        assertEquals("No owner with ID $petId", exception.message)
    }

    @Test
    fun `test getPetsByOwnerId returns list of pets`() {
        val ownerId = 101L
        val pets = listOf(
            Pet(id = 1L, name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = ownerId),
            Pet(id = 2L, name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = ownerId),
            Pet(id = 3L, name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = ownerId)
        )

        whenever(petDao.getPetsByOwnerId(ownerId, companyId)).thenReturn(pets)

        val result = petService.getPetsByOwnerId(ownerId, companyId)

        assertEquals(pets, result)
    }

    @Test
    fun `countPetsByType returns correct counts`() {
        val petCount = mapOf(PetType.CAT to 5, PetType.DOG to 3)

        whenever(petDao.countPetsByType(companyId)).thenReturn(petCount)

        val result = petService.countPetsByType(companyId)

        assertEquals(petCount, result)

        verify(petDao).countPetsByType(companyId)
    }

    @Test
    fun `test adoptMultiplePets calls DAO with correct parameters`() {

        val ownerId = 202L
        val petsId = listOf(1L, 2L, 3L)

        petService.adoptMultiplePets(companyId, ownerId, petsId)
        verify(petDao).adoptMultiplePets(companyId, ownerId, petsId)
    }

    @Test
    fun `test adoptMultiplePets throws exception for empty pet list`() {

        val ownerId = 202L
        val emptyPetsId = listOf<Long>()

        assertThrows<IllegalArgumentException> {
            petService.adoptMultiplePets(companyId, ownerId, emptyPetsId)
        }
    }

    @Test
    fun `test addMultiplePets with empty pet list`() {

        val emptyPetList = listOf<Pet>()

        petService.addMultiplePets(companyId, emptyPetList)

        verify(petDao).addMultiplePets(companyId, emptyPetList)
    }
}
