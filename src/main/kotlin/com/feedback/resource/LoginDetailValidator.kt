package com.feedback.resource


import org.springframework.http.HttpStatus

import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class LoginDetailValidator {

    private companion object ValidationConstants {
        const val COMPANY_ID_MINIMUM_THRESHOLD = 0L
        const val EMPLOYEE_ID_MINIMUM_THRESHOLD = 0L
    }

    fun validateCompanyId(companyId: Long) {
        if (companyId < COMPANY_ID_MINIMUM_THRESHOLD) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid company ID value: $companyId, it should be greater than ${COMPANY_ID_MINIMUM_THRESHOLD}."
            )
        }
    }

    fun validateEmployeeId(employeeId: Long?) {
        employeeId?.let {
            if (employeeId < EMPLOYEE_ID_MINIMUM_THRESHOLD) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid employee ID value: $employeeId, it should be greater than ${EMPLOYEE_ID_MINIMUM_THRESHOLD}."
                )
            }
        }
    }

}
