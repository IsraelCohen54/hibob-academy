package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import com.hibob.academy.dao.PetType
import org.springframework.stereotype.Service

@Service
class PetService(private val petDao: PetDao) {

    fun adoptPet(petId: Long, ownerId: Long, companyId: Long) {
        val currentOwnerId = petDao.getPetOwnerId(petId, companyId)
        currentOwnerId?.let {
            throw IllegalStateException("An owner for the pet already exists")
        } ?: petDao.adopt(petId, ownerId, companyId)
    }

    fun getOwnerByPetId(petId: Long, companyId: Long): Owner {
        return petDao.getPetOwner(petId, companyId)
            ?: throw IllegalStateException("No owner with ID $petId")
    }

    fun getOwnerPets(ownerId: Long, companyId: Long): List<Pet> {
        return petDao.getOwnerPets(ownerId, companyId)
    }

    fun countPetsByType(companyId: Long): Map<PetType, Int> {
        val petTypes = PetType.entries.toTypedArray()

        return petTypes.associateWith { type ->
            val pets = petDao.getPetsByType(type, companyId)
            pets.size
        }
    }
}
