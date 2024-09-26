package com.feedback.resource

import com.feedback.dao.LoggedInUser
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Cookie
import org.springframework.stereotype.Component


@Component
class ExtractCookieData {

    fun extractCompanyIdEmployeeId(cookies: Map<String, Cookie>): LoggedInUser {
        val companyId = cookies["company_id"]?.value?.toLong() ?: throw BadRequestException("Missing company_id")
        val employeeId = cookies["employee_id"]?.value?.toLong() ?: throw BadRequestException("Missing employee_id")
        return LoggedInUser(companyId, employeeId)
    }
}