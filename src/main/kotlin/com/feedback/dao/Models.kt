package com.feedback.dao

import com.hibob.nullability_exercises.nullSafeToUpper
import java.sql.Timestamp

data class RetrievedEmployee(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val department: DepartmentType
)

data class InsertEmployee(
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val department: DepartmentType
)

data class RetrievedFeedback(
    val id: Long,
    val department: DepartmentType?,
    val comment: String,
    val creationTimestamp: Timestamp,
    val status: StatusType,
    val employeeId: Long?
)

data class InsertFeedback(
    val department: DepartmentType?,
    val comment: String,
    )

data class Response(
    val id: Long? = null,
    val feedbackId: Long,
    val response: String
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
