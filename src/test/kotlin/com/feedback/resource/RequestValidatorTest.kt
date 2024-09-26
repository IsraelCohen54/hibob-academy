package com.feedback.resource

import com.feedback.dao.LoggedInUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class RequestValidatorTest {

    private val userRequestValidator = UserRequestValidator()

    private companion object {
        private const val LEGIT_COMPANY_ID_NUM = 1L
        private const val LEGIT_EMPLOYEE_ID_NUM = 1L
        private const val NON_LEGIT_COMPANY_ID_NUM = -1L
        private const val NON_LEGIT_EMPLOYEE_ID_NUM = -1L
    }

    @Test
    fun `validateCompanyId positive value positive`() {
        val exception = assertThrows<ResponseStatusException> {
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
            userRequestValidator.validateLoginValue(
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
            userRequestValidator.validateCommentValue(blankComment)
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
        }

        val expectedMessage =
            "Invalid comment value: $shortComment. It should be at least 21 characters long and not empty."

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)

        val actualMessage = exception.message

        assertTrue(actualMessage?.contains(expectedMessage) == true, "Expected message not found in actual message.")
    }

}