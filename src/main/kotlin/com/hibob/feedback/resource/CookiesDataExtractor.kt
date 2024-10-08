package com.hibob.feedback.resource

import com.hibob.feedback.dao.LoggedInUser
import jakarta.ws.rs.core.Cookie
import org.springframework.stereotype.Component


@Component
class CookiesDataExtractor {

    fun extractCompanyIdEmployeeId(cookies: Map<String, Cookie>): LoggedInUser {
        val companyId = cookies["company_id"]?.value?.toLong() ?: throw NumberFormatException("Missing company_id correct value")
        val employeeId = cookies["employee_id"]?.value?.toLong() ?: throw NumberFormatException("Missing employee_id correct value")
        return LoggedInUser(companyId, employeeId)
    }
}