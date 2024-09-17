package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.PetDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

class PetServiceTest {

    private val petDao: PetDao = mock {}
    private val petService = PetService(petDao)
    private val companyId = Random.nextLong()

    @Test
    fun `adoptPet should throw IllegalStateException if pet already has an owner`() {
        val petId = 1L
        val ownerId = 2L

        whenever(petDao.getPetOwnerId(petId, companyId)).thenReturn(3L)

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
        val expectedOwner = Owner(id = 1L, name = "John Doe", companyId = 1L, employeeId = "E123")

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
}
