package com.feedback.resource

import com.feedback.dao.DepartmentType
import com.feedback.service.FeedbackFetcher
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
import java.sql.Timestamp

@Component
@Path("/api/feedback")
@Produces("application/json")
@Consumes("application/json")
class FeedbackResource(
    private val cookiesDataExtractor: CookiesDataExtractor,
    private val userRequestValidator: UserRequestValidator,
    private val requestPreparetor: RequestPreparetor,
    private val feedbackInserter: FeedbackInserter,
    private val feedbackFetcher: FeedbackFetcher,
) {

    @POST
    fun submitFeedback(feedbackRequest: FeedbackRequest, @Context cookies: Map<String, Cookie>): Response {

        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        userRequestValidator.validateLoginValue(loggedInUser)
        userRequestValidator.validateCommentValue(feedbackRequest.comment)

        val feedbackCreationRequest = requestPreparetor.prepareRequestWithAnonymity(
            loggedInUser,
            feedbackRequest.isAnonymous,
            feedbackRequest.comment
        )
        feedbackInserter.insertFeedback(loggedInUser, feedbackCreationRequest)

        return Response.status(Status.OK).build()
    }

    @POST
    fun viewFeedback(feedbackRequest: FilterFeedbackRequest, @Context cookies: Map<String, Cookie>): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        userRequestValidator.validateLoginValue(loggedInUser)

        val filters = requestPreparetor.prepareViewWithFilterRequest(feedbackRequest)
        val feedbackMap = feedbackFetcher.filterFeedback(loggedInUser, filters)
        return Response.ok(feedbackMap).build()
    }
}

data class FeedbackRequest(val comment: String, val isAnonymous: Boolean)
data class FilterFeedbackRequest(val timestamp: Timestamp?, val department: DepartmentType?, val isAnonymous: Boolean?)
