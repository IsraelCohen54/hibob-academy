package com.hibob.feedback.dao

import com.hibob.academy.utils.JooqTable


class EmployeeTable(tableName: String = "employee") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val firstName = createVarcharField("first_name")
    val lastName = createVarcharField("last_name")
    val role = createVarcharField("role")
    val department = createVarcharField("department")

    companion object {
        val instance = EmployeeTable("employee")
    }
}

class FeedbackTable(tableName: String = "feedback") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val department = createVarcharField("department")
    val comment = createVarcharField("comment")
    val creationTimestamp = createTimestampField("creation_timestamp")
    val status = createVarcharField("status")
    val employeeId = createBigIntField("employee_id")

    companion object {
        val instance = FeedbackTable("feedback")
    }
}

class ResponseTable(tableName: String = "identified_feedback_response") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val feedbackId = createBigIntField("feedback_id")
    val response = createVarcharField("response")
    val responderId = createBigIntField("responder_id")

    companion object {
        val instance = ResponseTable("identified_feedback_response")
    }
}