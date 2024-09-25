package com.feedback.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.random.Random

@BobDbTest
class EmployeeDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val employeeTable = EmployeeTable.instance
    private val companyId = Random.nextLong()
    private val employeeDao = EmployeeDao(sql)

    private val dummyUserDetails: LoggedInUser = LoggedInUser(companyId, Random.nextLong())
    private val dummyEmployeeCreationRequest = EmployeeCreationRequest(firstName = "a", lastName = "b", role = RoleType.ADMIN, department = DepartmentType.PRODUCT)
    private val dummyPersistedEmployee = PersistedEmployee(id = 0,firstName = "a", lastName = "b", role = RoleType.ADMIN, department = DepartmentType.PRODUCT)

    @Test
    fun `test getEmployee return correct employee`() {
        val id = employeeDao.insertEmployee(dummyUserDetails, dummyEmployeeCreationRequest)
        val employee = employeeDao.getEmployee(dummyUserDetails.copy(employeeId=id))
        assertEquals(employee, dummyPersistedEmployee.copy(id = id))
    }

    @Test
    fun `test insertEmployee with same values return changed id`() {
        val id1 = employeeDao.insertEmployee(dummyUserDetails, dummyEmployeeCreationRequest)
        val id2 = employeeDao.insertEmployee(dummyUserDetails, dummyEmployeeCreationRequest)
        assertNotEquals(
            employeeDao.getEmployee(dummyUserDetails.copy(employeeId=id1)),
            employeeDao.getEmployee(dummyUserDetails.copy(employeeId=id2))
        )
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(employeeTable).where(employeeTable.companyId.eq(companyId)).execute()
    }
}