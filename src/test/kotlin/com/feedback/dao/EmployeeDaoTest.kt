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
    private val dummyEmployee = Employee(companyId = companyId, firstName = "a", lastName = "b", role = RoleType.ADMIN, department = DepartmentType.PRODUCT)

    @Test
    fun `test getEmployee return correct employee`() {
        val id = employeeDao.insertEmployee(dummyEmployee)
        val employee = employeeDao.getEmployee(companyId = companyId, employeeId = id)
        assertEquals(employee, dummyEmployee.copy(id = id))
    }

    @Test
    fun `test insertEmployee with same values return changed id`() {
        val id1 = employeeDao.insertEmployee(dummyEmployee)
        val id2 = employeeDao.insertEmployee(dummyEmployee)
        assertNotEquals(
            employeeDao.getEmployee(companyId, employeeId = id1),
            employeeDao.getEmployee(companyId, employeeId = id2)
        )
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(employeeTable).where(employeeTable.companyId.eq(companyId)).execute()
    }
}