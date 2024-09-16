package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.PetDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class PetServiceTest {

    private val petDao: PetDao = mock(PetDao::class.java)
    private val petService = PetService(petDao)

    @Test
    fun `adoptPet should throw IllegalStateException if pet already has an owner`() {
        val petId = 1L
        val ownerId = 2L
        val companyId = 10L

        whenever(petDao.getPetOwnerId(petId)).thenReturn(3L)

        assertThrows(IllegalStateException::class.java) {
            petService.adoptPet(petId, ownerId, companyId)
        }

        verify(petDao, never()).adopt(any(), any(), any())
    }

    @Test
    fun `adoptPet should call adopt on PetDao if pet does not have an owner`() {
        val petId = 1L
        val ownerId = 2L
        val companyId = 10L

        whenever(petDao.getPetOwnerId(petId)).thenReturn(null)

        petService.adoptPet(petId, ownerId, companyId)

        verify(petDao).adopt(eq(petId), eq(ownerId), eq(companyId))
    }

    @Test
    fun `getOwnerByPetId should return owner if present`() {
        val petId = 1L
        val expectedOwner = Owner(id = 1L, name = "John Doe", companyId = 1L, employeeId = "E123")

        whenever(petDao.getPetOwner(petId)).thenReturn(expectedOwner)

        val result = petService.getOwnerByPetId(petId)

        assertEquals(expectedOwner, result)
    }

    @Test
    fun `getOwnerByPetId should throw IllegalStateException when owner does not exist`() {

        val petId = 1L
        whenever(petDao.getPetOwner(petId)).thenReturn(null)

        val exception = assertThrows(IllegalStateException::class.java) {
            petService.getOwnerByPetId(petId)
        }
        assertEquals("No owner with ID $petId", exception.message)
    }
}
