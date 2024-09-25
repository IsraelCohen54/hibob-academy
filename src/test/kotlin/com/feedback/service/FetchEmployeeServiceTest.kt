package com.feedback.service

import com.feedback.dao.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class FetchEmployeeServiceTest {

    private val employeeDao: EmployeeDao = mock {}
    private val fetchEmployee = FetchEmployee(employeeDao)
    private val companyId = Random.nextLong()
    private val employeeId = Random.nextLong()

    private val dummyUserDetails = LoggedInUser(companyId, employeeId)
    private val dummyPersistedEmployee = PersistedEmployee(
        id = employeeId,
        firstName = "Bob",
        lastName = "Er",
        role = RoleType.MANAGER,
        department = DepartmentType.PRODUCT
    )

    @Test
    fun `getEmployeeDetails should return employee when found`() {

        whenever(employeeDao.getEmployee(dummyUserDetails)).thenReturn(dummyPersistedEmployee)

        val result = fetchEmployee.getEmployeeDetails(dummyUserDetails)

        assertEquals(dummyPersistedEmployee, result)
        verify(employeeDao).getEmployee(dummyUserDetails)
    }

    @Test
    fun `getEmployeeDetails should throw IllegalStateException when employee is not found`() {
        whenever(employeeDao.getEmployee(dummyUserDetails)).thenReturn(null)

        assertEquals("No employee found with ID $employeeId",
            assertThrows<IllegalStateException> { fetchEmployee.getEmployeeDetails(dummyUserDetails) }.message
        )

        verify(employeeDao).getEmployee(dummyUserDetails)
    }
}
