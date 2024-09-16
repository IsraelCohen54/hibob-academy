package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.PetDao
import org.springframework.stereotype.Service

@Service
class PetService(private val petDao: PetDao) {

    fun adoptPet(petId: Long, ownerId: Long, companyId: Long) {
        val currentOwnerId = petDao.getPetOwnerId(petId)
        currentOwnerId?.let {
            throw IllegalStateException()
        } ?: petDao.adopt(petId, ownerId, companyId)
    }

    fun getOwnerByPetId(petId: Long): Owner {
        val owner = petDao.getPetOwner(petId)
        return owner ?: throw IllegalStateException("No owner with ID $petId")
    }
}
