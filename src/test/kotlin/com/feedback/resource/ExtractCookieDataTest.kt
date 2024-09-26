package com.feedback.resource

import jakarta.ws.rs.core.Cookie
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExtractCookiesDataTest {

    private val cookiesDataExtractor = CookiesDataExtractor()
    private val extractCookieData = ExtractCookieData()
    private val cookiesDataExtractor = CookiesDataExtractor()

    @Test
    fun `should extract companyId and employeeId from cookies`() {
        val cookies = mapOf(
            "company_id" to Cookie.Builder("company_id").value("1").build(),
            "employee_id" to Cookie.Builder("employee_id").value("2").build()
        )

        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        val loggedInUser = extractCookieData.extractCompanyIdEmployeeId(cookies)

        assertEquals(1L, loggedInUser.companyId)
        assertEquals(2L, loggedInUser.employeeId)
    }

    @Test
    fun `should throw NumberFormatException when company_id is missing`() {
        val cookies = mapOf("employee_id" to Cookie.Builder("employee_id").value("2").build())

        assertEquals(
            "Missing company_id correct value",
            assertThrows<NumberFormatException> {
                cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
                extractCookieData.extractCompanyIdEmployeeId(cookies)
                cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
            }.message
        )
    }

    @Test
    fun `should throw NumberFormatException when employee_id is missing`() {
        val cookies = mapOf("company_id" to Cookie.Builder("company_id").value("1").build())

        assertEquals(
            "Missing employee_id correct value",
            assertThrows<NumberFormatException> {
                cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
                cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
                extractCookieData.extractCompanyIdEmployeeId(cookies)
            }.message
        )
    }
}
