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

    // Test fields
    private val userDetails: LoggedInUser = LoggedInUser(companyId, Random.nextLong())
    private val dummyInsertEmployee = EmployeeCreationRequest(firstName = "a", lastName = "b", role = RoleType.ADMIN, department = DepartmentType.PRODUCT)
    private val dummyRetrievedEmployee = RetrieveEmployeeRequest(id = 0,firstName = "a", lastName = "b", role = RoleType.ADMIN, department = DepartmentType.PRODUCT)

    @Test
    fun `test getEmployee return correct employee`() {
        val id = employeeDao.insertEmployee(userDetails, dummyInsertEmployee)
        val employee = employeeDao.getEmployee(userDetails.copy(employeeId=id))
        assertEquals(employee, dummyRetrievedEmployee.copy(id = id))
    }

    @Test
    fun `test insertEmployee with same values return changed id`() {
        val id1 = employeeDao.insertEmployee(userDetails, dummyInsertEmployee)
        val id2 = employeeDao.insertEmployee(userDetails, dummyInsertEmployee)
        assertNotEquals(
            employeeDao.getEmployee(userDetails.copy(employeeId=id1)),
            employeeDao.getEmployee(userDetails.copy(employeeId=id2))
        )
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(employeeTable).where(employeeTable.companyId.eq(companyId)).execute()
    }
}