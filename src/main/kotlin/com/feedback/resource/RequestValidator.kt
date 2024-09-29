package com.feedback.resource


import com.feedback.dao.LoggedInUser
import com.feedback.dao.RoleType
import com.feedback.dao.StatusType
import com.feedback.service.EmployeeFetcher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException


@Component
class RequestValidator(private val employeeFetcher: EmployeeFetcher) {

    private companion object ValidationConstants {
        const val COMPANY_ID_MINIMUM_THRESHOLD = 0L
        const val EMPLOYEE_ID_MINIMUM_THRESHOLD = 0L
        const val FEEDBACK_ID_MINIMUM_THRESHOLD = 0L
    }

    fun validateLoginValue(loggedInUser: LoggedInUser) {
        this.validateCompanyId(loggedInUser.companyId)
        this.validateEmployeeId(loggedInUser.employeeId)
    }

    fun validatePermission(loggedInUser: LoggedInUser) {
        val userRole = employeeFetcher.getEmployeeDetails(loggedInUser).role
        if (userRole != RoleType.ADMIN && userRole != RoleType.MANAGER)
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden")
    }

    fun validateCommentValue(comment: String) {
        if (comment.isBlank() || comment.length <= 20) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid comment value: $comment. It should be at least 21 characters long and not empty."
            )
        }
    }

    fun validateStatusUpdater(statusUpdateRequest: StatusUpdateRequest) {
        if (statusUpdateRequest.feedbackId < FEEDBACK_ID_MINIMUM_THRESHOLD) {
            throw IllegalArgumentException("Feedback ID must be a positive number.")
        }

        try {
            StatusType.fromString(statusUpdateRequest.status)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status value: ${statusUpdateRequest.status}")
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
