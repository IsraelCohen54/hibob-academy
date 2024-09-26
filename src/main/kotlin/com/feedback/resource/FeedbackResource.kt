package com.feedback.resource

import com.feedback.dao.FeedbackCreationRequest
import com.feedback.dao.LoggedInUser
import com.feedback.service.EmployeeFetcher
import com.feedback.service.FeedbackInserter
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Cookie
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import org.springframework.stereotype.Component

@Component
@Path("/api/feedback")
@Produces("application/json")
@Consumes("application/json")
class FeedbackResource(
    private val feedbackInserter: FeedbackInserter,
    private val employeeFetcher: EmployeeFetcher,
    private val loginDetailValidator: LoginDetailValidator,
    private val cookiesDataExtractor: CookiesDataExtractor
) {

    @POST
    fun submitFeedback(
        feedbackRequest: FeedbackRequest,
        @Context cookies: Map<String, Cookie>
    ): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)

        loginDetailValidator.validateCompanyId(loggedInUser.companyId)
        loginDetailValidator.validateEmployeeId(loggedInUser.employeeId)

        val department = fetchEmployeeDepartmentByAnonymity(loggedInUser, feedbackRequest.isAnonymous)
        val feedbackCreationRequest = FeedbackCreationRequest(department, feedbackRequest.comment)

        feedbackInserter.insertFeedback(loggedInUser, feedbackCreationRequest)

        return Response.status(Status.OK).build()
    }

    private fun fetchEmployeeDepartmentByAnonymity(loggedInUser: LoggedInUser, isAnonymous: Boolean) =
        if (isAnonymous) null else employeeFetcher.getEmployeeDetails(loggedInUser).department


}

data class FeedbackRequest(val comment: String, val isAnonymous: Boolean)
