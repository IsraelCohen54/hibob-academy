package com.hibob.academy.dao

import com.hibob.academy.utils.JooqTable

class PetTable(tableName: String = "pets") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")
    val type = createVarcharField("type")
    val companyId = createBigIntField("company_id")
    val dateOfArrival = createLocalDateField("date_of_arrival")
    val ownerId = createBigIntField("owner_id")

    companion object {
        val petInstance = PetTable()
    }
}

class OwnerTable(tableName: String = "owners") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")
    val companyId = createBigIntField("company_id")
    val employeeId = createVarcharField("employee_id")

    companion object {
        val ownerInstance = OwnerTable()
    }
}

class VaccineTable(tableName: String = "vaccine") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")

    companion object {
        val vaccineInstance = VaccineTable()
    }
}

class VaccineToPetTable(tableName: String = "vaccine_to_pet") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")
    val vaccinationDate =  createLocalDateField("vaccination_date")
    val petId = createBigIntField("pet_id")

    companion object {
        val vaccineToPetInstance = VaccineToPetTable()
    }
}