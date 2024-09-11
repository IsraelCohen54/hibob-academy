package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

// column - owneridtopets
// employee_is - string
// owner_id - uuid

@BobDbTest
class OwnerDaoTest @Inject constructor(private val sql: DSLContext)  {

    private val table = OwnerTable()
    private val companyId = Random.nextLong()
    private val oD = OwnerDao(sql)

    @Test
    fun `insert owner test`() {
        val owner1 = OwnerDao.Owner("Bob1", companyId, "123")
        oD.insertOwner("Bob1", companyId, "123")
        val compareLists: List<OwnerDao.Owner?> = listOf(owner1)
        assertEquals(compareLists, oD.getOwners(companyId))
    }

    @Test
    fun `insert owner size test`() {
        val owner1 = OwnerDao.Owner("Bob1", companyId, "123")
        val owner2 = OwnerDao.Owner("Bob2", companyId, "124")
        val owner3 = OwnerDao.Owner("Bob3", companyId, "125")
        oD.insertOwner(owner1.name, companyId, owner1.employeeId)
        oD.insertOwner(owner2.name, companyId, owner2.employeeId)
        oD.insertOwner(owner3.name, companyId, owner3.employeeId)
        assertEquals(3, oD.getOwners(companyId).size)
    }

    @Test
    fun `get owner test without any owner`() {
        val ownerVacantList : List<OwnerDao.Owner?> = emptyList()
        assertEquals(ownerVacantList, oD.getOwners(companyId))
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}