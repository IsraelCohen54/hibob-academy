package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

@BobDbTest
class OwnerDaoTest @Inject constructor(private val sql: DSLContext)  {

    private val table = OwnerTable()
    private val companyId = Random.nextLong()
    private val ownerDao = OwnerDao(sql)

    @Test
    fun `insert owner`() {
        ownerDao.insertOwner("Bob1", companyId, "123")
        val owner1 = ownerDao.getOwners(companyId)[0]
        val compareLists: List<Owner?> = listOf(owner1)
        assertEquals(compareLists, ownerDao.getOwners(companyId))
    }

    @Test
    fun `get owner test without any owner`() {
        val ownerVacantList : List<Owner?> = emptyList()
        assertEquals(ownerVacantList, ownerDao.getOwners(companyId))
    }

    @Test
    fun `insert same owner by unique key, changed name value, should not be added to DB using conflict`() {
        val owner1 = Owner(1, "Bob1", companyId, "123")
        ownerDao.insertOwner(owner1.name, companyId, owner1.employeeId)
        ownerDao.insertOwner("other name, same unique key", companyId, owner1.employeeId)
        assertEquals(1,ownerDao.getOwners(companyId).size)
        val owners = ownerDao.getOwners(companyId)
        assertTrue(owners.any { it?.name == owner1.name }, "Owner with name ${owner1.name} does not exist")
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}