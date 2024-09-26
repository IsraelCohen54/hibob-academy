package com.feedback.resource

import com.feedback.dao.DepartmentType
import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedEmployee
import com.feedback.dao.RoleType
import com.feedback.service.EmployeeFetcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class RequestValidatorTest {

    private val employeeFetcher: EmployeeFetcher = mock()
    private val requestValidator = RequestValidator(employeeFetcher)
    private val userRequestValidator = UserRequestValidator()
    private val employeeFetcher: EmployeeFetcher = mock()
    private val requestValidator = RequestValidator(employeeFetcher)

    private companion object {
        private const val LEGIT_COMPANY_ID_NUM = 1L
        private const val LEGIT_EMPLOYEE_ID_NUM = 1L
        private const val NON_LEGIT_COMPANY_ID_NUM = -1L
        private const val NON_LEGIT_EMPLOYEE_ID_NUM = -1L
    }

    @Test
    fun `validateCompanyId positive value positive`() {
        val exception = assertThrows<ResponseStatusException> {
            requestValidator.validateLoginValue(
            requestValidator.validateLoginValue(
            userRequestValidator.validateLoginValue(
                LoggedInUser(
                    companyId = NON_LEGIT_COMPANY_ID_NUM,
                    employeeId = LEGIT_EMPLOYEE_ID_NUM
                )
            )
        }
        assertEquals(
            "Invalid company ID value: ${NON_LEGIT_COMPANY_ID_NUM}, it should be greater than 0.",
            exception.reason

        )
    }

    @Test
    fun `validateEmployeeId positive value positive`() {
        val exception = assertThrows<ResponseStatusException> {
            requestValidator.validateLoginValue(
            userRequestValidator.validateLoginValue(
            requestValidator.validateLoginValue(
                LoggedInUser(
                    companyId = LEGIT_COMPANY_ID_NUM,
                    employeeId = NON_LEGIT_EMPLOYEE_ID_NUM
                )
            )
        }

        assertEquals(
            "Invalid employee ID value: ${NON_LEGIT_EMPLOYEE_ID_NUM}, it should be greater than 0.",
            exception.reason
        )
    }

    @Test
    fun `validateCommentValue throws exception if comment is blank`() {
        val blankComment = ""

        val exception = assertThrows<ResponseStatusException> {
            requestValidator.validateCommentValue(blankComment)
            userRequestValidator.validateCommentValue(blankComment)
            requestValidator.validateCommentValue(blankComment)
        }

        val expectedMessage =
            "Invalid comment value: $blankComment. It should be at least 21 characters long and not empty."

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)

        val actualMessage = exception.message

        assertTrue(actualMessage?.contains(expectedMessage) == true, "Expected message not found in actual message.")
    }

    @Test
    fun `validateCommentValue throws exception if comment is too short`() {
        val shortComment = "Too short"
        val exception = assertThrows<ResponseStatusException> {
            userRequestValidator.validateCommentValue(shortComment)
            requestValidator.validateCommentValue(shortComment)
            requestValidator.validateCommentValue(shortComment)
        }

        val expectedMessage =
            "Invalid comment value: $shortComment. It should be at least 21 characters long and not empty."

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)

        val actualMessage = exception.message

        assertTrue(actualMessage?.contains(expectedMessage) == true, "Expected message not found in actual message.")
    }

    @Test
    fun `validatePermission throws exception for non-admin or non-manager user`() {
        val loggedInUser = LoggedInUser(companyId = 1L, employeeId = 1L)

        whenever(employeeFetcher.getEmployeeDetails(loggedInUser)).thenReturn(PersistedEmployee(
            id = 1L,
            firstName = "a",
            lastName = "b",
            role = RoleType.EMPLOYEE, department = DepartmentType.IT)
        )

        val exception = assertThrows<ResponseStatusException> {
            requestValidator.validatePermission(loggedInUser)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        assertEquals("Forbidden", exception.reason)
    }

    @Test
    fun `validatePermission throws exception for non-admin or non-manager user`() {
        val loggedInUser = LoggedInUser(companyId = 1L, employeeId = 1L)

        whenever(employeeFetcher.getEmployeeDetails(loggedInUser)).thenReturn(
            PersistedEmployee(
                id = 1L,
                firstName = "a",
                lastName = "b",
                role = RoleType.EMPLOYEE, department = DepartmentType.IT
            )
        )

        val exception = assertThrows<ResponseStatusException> {
            requestValidator.validatePermission(loggedInUser)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        assertEquals("Forbidden", exception.reason)
    }

    @Test
    fun `validateStatusUpdater should throw exception for feedbackId below threshold`() {
        val statusUpdateRequest = StatusUpdateRequest(status = "reviewed", feedbackId = -1)

        assertEquals(
            "Feedback ID must be a positive number.",
            assertThrows<IllegalArgumentException> {
                requestValidator.validateStatusUpdater(statusUpdateRequest)
            }.message
        )
    }

    @Test
    fun `validateStatusUpdater should throw exception for invalid status`() {
        val statusUpdateRequest = StatusUpdateRequest(status = "invalid_status", feedbackId = 1)

        assertEquals(
            "Invalid status value: invalid_status",
            assertThrows<IllegalArgumentException> {
                requestValidator.validateStatusUpdater(statusUpdateRequest)
            }.message
        )
    }

}