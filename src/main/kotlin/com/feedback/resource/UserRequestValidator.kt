package com.feedback.resource


import com.feedback.dao.LoggedInUser
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class UserRequestValidator {

    private companion object ValidationConstants {
        const val COMPANY_ID_MINIMUM_THRESHOLD = 0L
        const val EMPLOYEE_ID_MINIMUM_THRESHOLD = 0L
    }

    fun validateLoginValue(loggedInUser: LoggedInUser) {
        this.validateCompanyId(loggedInUser.companyId)
        this.validateEmployeeId(loggedInUser.employeeId)
    }

    fun validateCommentValue(comment:String) {
        if (comment.isBlank() || comment.length <= 20) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid comment value: $comment. It should be at least 21 characters long and not empty.")
        }
    }


    private fun validateCompanyId(companyId: Long) {
        if (companyId < COMPANY_ID_MINIMUM_THRESHOLD) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid company ID value: $companyId, it should be greater than ${COMPANY_ID_MINIMUM_THRESHOLD}."
            )
        }
    }

    private fun validateEmployeeId(employeeId: Long?) {
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
