package com.feedback.dao

import com.hibob.nullability_exercises.nullSafeToUpper
import java.sql.Timestamp

data class RetrieveEmployeeRequest(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val department: DepartmentType
)

data class EmployeeCreationRequest(
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val department: DepartmentType
)

data class RetrieveFeedbackRequest(
    val id: Long,
    val department: DepartmentType?,
    val comment: String,
    val creationTimestamp: Timestamp,
    val status: StatusType,
    val employeeId: Long?
)

data class FeedbackCreationRequest(
    val department: DepartmentType?,
    val comment: String,
    )

data class ResponseCreationRequest(
    val feedbackId: Long,
    val response: String,
    val responderId: Long
)

data class RetrieveResponseRequest(
    val id: Long,
    val feedbackId: Long,
    val response: String,
    val responderId: Long
)

data class LoggedInUser(val companyId: Long, val employeeId: Long?)

enum class RoleType {
    ADMIN,
    MANAGER,
    EMPLOYEE;

    companion object {
        // convert a String to a Role
        fun fromString(value: String): RoleType {
            return RoleType.valueOf(value.nullSafeToUpper())
        }
    }
}

enum class DepartmentType {
    IT,
    PRODUCT,
    CX;

    companion object {
        // convert a String to a Department
        fun fromString(value: String): DepartmentType {
            return DepartmentType.valueOf(value.nullSafeToUpper())
        }
    }
}

enum class StatusType {
    NOT_SOLVED,
    PAUSED,
    SOLVED;

    companion object {
        // convert a String to a Status
        fun fromString(value: String): StatusType {
            return StatusType.valueOf(value.nullSafeToUpper())
        }
    }
}
