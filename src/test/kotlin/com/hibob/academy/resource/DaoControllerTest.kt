package com.hibob.academy.resource

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.PetDao
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import kotlin.random.Random

class DaoControllerTest {

    private lateinit var petDao: PetDao
    private lateinit var daoController: DaoController
    private val companyId = Random.nextLong()

    @BeforeEach
    fun setUp() {
        petDao = mock(PetDao::class.java)
        daoController = DaoController()
        daoController.setPetDao(petDao)
    }

    @Test
    fun adoptPet_whenPetDoesNotHaveOwner_returnsSuccessResponse() {
        val petId = 1L
        val ownerId = 2L

        whenever(petDao.getPetOwnerId(petId)).thenReturn(null)

        val response: Response = daoController.adoptPet(petId, ownerId, companyId)

        assert(response.status == Response.Status.OK.statusCode)
        assert(response.entity == "Pet $petId adopted by owner $ownerId")

        verify(petDao).adopt(petId, ownerId, companyId)
    }

    @Test
    fun adoptPet_whenPetAlreadyHasOwner_returnsConflictResponse() {
        val petId = 1L
        val ownerId = 2L
        val companyId = 10L

        whenever(petDao.getPetOwnerId(petId)).thenReturn(3L)

        val response: Response = daoController.adoptPet(petId, ownerId, companyId)

        // Verify response
        assert(response.status == Response.Status.CONFLICT.statusCode)
        assert(response.entity == "Pet with ID $petId has already an owner")

        verify(petDao, never()).adopt(any(), any(), any())
    }

    @Test
    fun getOwnerByPetId_whenOwnerExists_returnsOwnerDetails() {
        val petId = 1L
        val owner = Owner(id = 2L, name = "John Doe", employeeId = "E123", companyId = companyId)

        whenever(petDao.getPetOwner(petId)).thenReturn(owner)

        val response: Response = daoController.getOwnerByPetId(petId)

        // Verify response
        assert(response.status == Response.Status.OK.statusCode)
        assert(response.entity == owner)

        verify(petDao).getPetOwner(petId)
    }

    @Test
    fun getOwnerByPetId_whenOwnerDoesNotExist_returnsNotFoundResponse() {
        val petId = 1L

        whenever(petDao.getPetOwner(petId)).thenReturn(null)

        val response: Response = daoController.getOwnerByPetId(petId)

        assert(response.status == Response.Status.NOT_FOUND.statusCode)
        assert(response.entity == "Owner for pet with ID $petId not found")

        verify(petDao).getPetOwner(petId)
    }
}
