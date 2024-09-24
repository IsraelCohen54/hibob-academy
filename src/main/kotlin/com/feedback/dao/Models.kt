package com.feedback.dao

import com.hibob.nullability_exercises.nullSafeToUpper
import java.sql.Timestamp

data class Employee(
    val id: Long? = null,
    val companyId: Long,
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val department: DepartmentType
)


sealed class Feedback {
    abstract val companyId: Long
    abstract val comment: String
    abstract val department: DepartmentType?
    abstract val employeeId: Long?

    data class RetrieveFeedback(
        val id: Long,
        override val companyId: Long,
        override val department: DepartmentType?,
        override val comment: String,
        val creationTimestamp: Timestamp,
        val status: StatusType,
        override val employeeId: Long?
    ) : Feedback()

    data class InsertFeedback(
        override val companyId: Long,
        override val department: DepartmentType?,
        override val comment: String,
        override val employeeId: Long?
    ) : Feedback()
}


data class Response(
    val id: Long? = null,
    val companyId: Long,
    val feedbackId: Long,
    val response: String
)

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
