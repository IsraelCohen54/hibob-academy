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
        val owner1 = Owner(1, "Bob1", companyId, "123")
        ownerDao.insertOwner(owner1.name, companyId, owner1.employeeId)
        val compareLists: List<Owner?> = listOf(owner1)
        assertEquals(compareLists, ownerDao.getOwners(companyId))
    }

    @Test
    fun `get owner test without any owner`() {
        val ownerVacantList : List<Owner?> = emptyList()
        assertEquals(ownerVacantList, ownerDao.getOwners(companyId))
    }

    @Test
    fun `insert same owner - do not add to DB using conflict`() {
        val owner1 = Owner(1, "Bob1", companyId, "123")
        ownerDao.insertOwner(owner1.name, companyId, owner1.employeeId)
        ownerDao.insertOwner(owner1.name, companyId, owner1.employeeId)
        assertEquals(1,ownerDao.getOwners(companyId).size)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}