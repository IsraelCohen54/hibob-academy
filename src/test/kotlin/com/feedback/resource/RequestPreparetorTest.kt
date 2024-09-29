package com.feedback.resource

import com.feedback.dao.DepartmentType
import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedEmployee
import com.feedback.dao.RoleType
import com.feedback.service.AnonymousFilter
import com.feedback.service.DepartmentFilter
import com.feedback.service.EmployeeFetcher
import com.feedback.service.FromDateFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp

class RequestPreparetorTest {

    private val employeeFetcher: EmployeeFetcher = mock()
    private val requestPreparetor = RequestPreparetor(employeeFetcher)

    private companion object {
        private const val DUMMY_COMPANY_ID = 1L
        private const val DUMMY_EMPLOYEE_ID = 1L
        private val DUMMY_DEPARTMENT = DepartmentType.IT
    }

    private val comment = "This is a valid, long enough, comment."
    private val loggedInUser = LoggedInUser(companyId = DUMMY_COMPANY_ID, employeeId = DUMMY_EMPLOYEE_ID)

    @Test
    fun `prepareRequestWithAnonymity returns request with null department when anonymous`() {

        val feedbackRequest = requestPreparetor.prepareRequestWithAnonymity(loggedInUser, isAnonymous = true, comment)

        assertNull(feedbackRequest.department)
        assertEquals(comment, feedbackRequest.comment)
    }

    @Test
    fun `prepareRequestWithAnonymity returns request with valid department when not anonymous`() {

        whenever(employeeFetcher.getEmployeeDetails(loggedInUser)).thenReturn(
            PersistedEmployee(
                id = 0, firstName = "0", lastName = "v", role = RoleType.ADMIN,
                department = DUMMY_DEPARTMENT
            )
        )

        val feedbackRequest = requestPreparetor.prepareRequestWithAnonymity(loggedInUser, isAnonymous = false, comment)

        assertEquals(DUMMY_DEPARTMENT, feedbackRequest.department)

        verify(employeeFetcher).getEmployeeDetails(loggedInUser)
    }

    @Test
    fun `prepareRequestWithAnonymity throws exception when employee not found`() {

        whenever(employeeFetcher.getEmployeeDetails(loggedInUser)).thenThrow(
            IllegalStateException("No employee found with ID ${loggedInUser.employeeId}")
        )

        val exception = assertThrows<IllegalStateException> {
            requestPreparetor.prepareRequestWithAnonymity(loggedInUser, isAnonymous = false, comment)
        }

        assertEquals("No employee found with ID ${loggedInUser.employeeId}", exception.message)

        verify(employeeFetcher).getEmployeeDetails(loggedInUser)
    }

    @Test
    fun `prepareViewWithFilterRequest returns empty list when all fields are null`() {
        val filters = FilterFeedbackRequest(
            timestamp = null,
            department = null,
            isAnonymous = null
        )

        val result = requestPreparetor.prepareViewWithFilterRequest(filters)

        assertEquals(0, result.size)
    }

    @Test
    fun `prepareViewWithFilterRequest returns correct filters when all fields are provided`() {
        val filters = FilterFeedbackRequest(
            timestamp = Timestamp.valueOf("2024-09-26 12:00:00"),
            department = DepartmentType.IT, // Assuming IT is a valid enum constant
            isAnonymous = true
        )

        val result = requestPreparetor.prepareViewWithFilterRequest(filters)

        assertEquals(3, result.size)
        assert(result[0] is FromDateFilter)
        assert(result[1] is DepartmentFilter)
        assert(result[2] is AnonymousFilter)
    }
}
