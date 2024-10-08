package com.hibob.feedback.resource

import com.hibob.feedback.dao.ResponseCreationRequest
import com.hibob.feedback.service.ForceResponseInserter
import com.hibob.feedback.service.ResponseFetcher
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Cookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component

@Component
@Produces("application/json")
@Consumes("application/json")
@Path("/api/feedback")
class ResponseResource(
    private val cookiesDataExtractor: CookiesDataExtractor,
    private val requestValidator: RequestValidator,
    private val forceResponseInserter: ForceResponseInserter,
    private val responseFetcher: ResponseFetcher
) {

    @PATCH
    @Path("/{feedback_id}/response")
    fun submitResponse(
        @PathParam("feedback_id") feedbackId: Long,
        responseCreationRequest: ResponseCreationRequest,
        @Context cookies: Map<String, Cookie>
    ): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        requestValidator.validateLoginValue(loggedInUser)
        requestValidator.validatePermission(loggedInUser)

        forceResponseInserter.createOrUpdateResponse(loggedInUser, responseCreationRequest)

        return Response.ok().entity("Response submitted successfully").build()
    }

    @GET
    @Path("/{feedback_id}/response")
    fun viewResponse(
        @PathParam("feedback_id") feedbackId: Long,
        @Context cookies: Map<String, Cookie>
    ): Response {
        val loggedInUser = cookiesDataExtractor.extractCompanyIdEmployeeId(cookies)
        requestValidator.validateLoginValue(loggedInUser)
        requestValidator.validatePermission(loggedInUser)

        val feedbackResponse = responseFetcher.getResponseDetails(loggedInUser, feedbackId)

        return Response.ok(feedbackResponse).build()
    }
}
