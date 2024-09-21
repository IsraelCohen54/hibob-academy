package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    fun `insert same owner by unique key with changed name value, should throw exception without changing data`() {

        val ownerId1 = ownerDao.insertOwner("Bob1", companyId, "123")

        assertEquals("Failed to retrieve the generated ID.",
        assertThrows<IllegalStateException> {
            ownerDao.insertOwner("other name, same unique key", companyId, "123")
        }.message
        )

        val fetchedOwner = ownerDao.getOwnerById(ownerId1, companyId)
        fetchedOwner?.let {
            assertEquals("Bob1", it.name)
        }
    }

    @Test
    fun `test getOwnerById returns correct owner`() {
        ownerDao.insertOwner("Bob1", companyId, "123")

        val ownerId = ownerDao.getOwnerId(companyId, "123")
        ownerId?.let {
            val fetchedOwner = ownerDao.getOwnerById(ownerId, companyId)

            assertEquals(
                Owner(id = ownerId, name = "Bob1", employeeId = "123", companyId = companyId),
                fetchedOwner,
                "The retrieved owner should match the inserted owner"
            )
        }
    }

    @Test
    fun `test getOwnerId returns correct owner ID`() {
        ownerDao.insertOwner("Bob1", companyId, "123")
        val fetchedOwnerId = ownerDao.getOwnerId(companyId, "123")

        fetchedOwnerId?.let {
            val expectedOwnerId = ownerDao.getOwners(companyId).firstOrNull()?.id
            assertEquals(expectedOwnerId, fetchedOwnerId,
                "The retrieved owner ID should match the expected owner ID"
            )
        }
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table).where(table.companyId.eq(companyId)).execute()
    }
}