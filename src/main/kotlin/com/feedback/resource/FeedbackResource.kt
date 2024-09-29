package com.feedback.resource

import com.feedback.dao.DepartmentType
import com.feedback.dao.StatusType
import com.feedback.service.FeedbackFetcher
import com.feedback.service.FeedbackInserter
import com.feedback.service.FeedbackUpdater
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Cookie
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
@Produces("application/json")
@Consumes("application/json")
@Path("/api/feedback")
class FeedbackResource(
    private val cookiesDataExtractor: CookiesDataExtractor,
    private val requestValidator: RequestValidator,
    private val requestPreparetor: RequestPreparetor,
    private val feedbackInserter: FeedbackInserter,
    private val feedbackFetcher: FeedbackFetcher,
    private val feedbackUpdater: FeedbackUpdater,

    ) {
    @POST
    fun submitFeedback(feedbackRequest: FeedbackRequest, @Context cookies: Map<String, Cookie>): Response {

        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        requestValidator.validateLoginValue(loggedInUser)
        requestValidator.validateCommentValue(feedbackRequest.comment)

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
        requestValidator.validateLoginValue(loggedInUser)
        requestValidator.validatePermission(loggedInUser)

        val filters = requestPreparetor.prepareViewWithFilterRequest(feedbackRequest)
        val feedbackMap = feedbackFetcher.filterFeedback(loggedInUser, filters)
        return Response.ok(feedbackMap).build()
    }

    @Path("/status")
    @GET
    fun getFeedbackStatus(@Context cookies: Map<String, Cookie>): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        requestValidator.validateLoginValue(loggedInUser)

        val feedbackStatusMap = feedbackFetcher.getUserFeedbacks(loggedInUser)

        return Response.ok(feedbackStatusMap).build()
    }

    @PATCH
    @Path("/status")
    fun updateFeedbackStatus(statusUpdateRequest: StatusUpdateRequest, @Context cookies: Map<String, Cookie>): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        requestValidator.validateLoginValue(loggedInUser)
        requestValidator.validateStatusUpdater(statusUpdateRequest)

        feedbackUpdater.updateFeedbackStatus(
            loggedInUser, statusUpdateRequest.feedbackId, StatusType.fromString(statusUpdateRequest.status)
        )

        return Response.ok().entity("Status updated successfully").build()
    }
}

data class StatusUpdateRequest(val status: String, val feedbackId: Long)
data class FeedbackRequest(val comment: String, val isAnonymous: Boolean)
data class FilterFeedbackRequest(val timestamp: Timestamp?, val department: DepartmentType?, val isAnonymous: Boolean?)
