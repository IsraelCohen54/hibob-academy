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
    fun `test getOwnerPets returns list of pets`() {
        val ownerId = 101L
        val pets = listOf(
            Pet(id = 1L, name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = ownerId),
            Pet(id = 2L, name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = ownerId),
            Pet(id = 3L, name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = ownerId)
        )

        whenever(petDao.getOwnerPets(ownerId, companyId)).thenReturn(pets)

        val result = petService.getOwnerPets(ownerId, companyId)

        assertEquals(pets, result)
    }

    @Test
    fun `test countPetsByType returns correct pet counts`() {
        val petsByType: List<List<Pet>> = listOf(listOf(
                Pet(id = 1L, name = "Rex1", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2024, 1, 6), ownerId = null),
                Pet(id = 3L, name = "Rex3", type = PetType.DOG, companyId = companyId, dateOfArrival = LocalDate.of(2026, 3, 5), ownerId = null))
            , listOf(
                Pet(id = 2L, name = "Rex2", type = PetType.CAT, companyId = companyId, dateOfArrival = LocalDate.of(2022, 2, 5), ownerId = null)
            )
        )
        whenever(petDao.getPetsByType(PetType.DOG, companyId)).thenReturn(petsByType[0])
        whenever(petDao.getPetsByType(PetType.CAT, companyId)).thenReturn(petsByType[1])

        val resultedMap = petService.countPetsByType(companyId)
        val expected = mapOf(
            PetType.DOG to (petsByType[0].size),
            PetType.CAT to (petsByType[1].size)
        )
        assertEquals(expected, resultedMap)
    }
}
