package com.feedback.resource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class LoginDetailValidatorTest {

    private val loginDetailValidator = LoginDetailValidator()

    @Test
    fun validateCompanyId() {
        val invalidCompanyId = -1L

        val exception = assertThrows<ResponseStatusException> {
            loginDetailValidator.validateCompanyId(invalidCompanyId)
        }
        assertEquals(
            "Invalid company ID value: $invalidCompanyId, it should be greater than 0.",
            exception.reason

        )
    }


    @Test
    fun validateEmployeeId() {
        val invalidEmployeeId = -1L
        val exception = assertThrows<ResponseStatusException> {
            loginDetailValidator.validateEmployeeId(invalidEmployeeId)
        }

        assertEquals(
            "Invalid employee ID value: $invalidEmployeeId, it should be greater than 0.",
            exception.reason
        )

    }
}