package com.feedback.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class ResponseDao(private val sql: DSLContext) {

    private val responseTable = ResponseTable.instance
    private val retrieveResponseTableMapper = RecordMapper<Record, RetrieveResponseRequest> { record ->
        RetrieveResponseRequest(
            id = record[responseTable.id],
            feedbackId = record[responseTable.feedbackId],
            response = record[responseTable.response],
            responderId = record[responseTable.responderId]
        )
    }

    fun forceInsertResponse(userDetails: LoggedInUser, response: ResponseCreationRequest): Long {
        return sql.insertInto(responseTable)
            .set(responseTable.companyId, userDetails.companyId)
            .set(responseTable.feedbackId, response.feedbackId)
            .set(responseTable.response, response.response)
            .set(responseTable.responderId, userDetails.employeeId)
            .onConflict(responseTable.feedbackId, responseTable.responderId)
            .doUpdate()
            .set(responseTable.response, response.response)
            .returning(responseTable.id)
            .fetchOne()
            ?.get(responseTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated or updated response ID.")
    }

    fun getResponseByFeedbackId(userDetails: LoggedInUser, feedbackId: Long): RetrieveResponseRequest? {
        return sql.selectFrom(responseTable)
            .where(
                responseTable.feedbackId.eq(feedbackId)
                    .and(responseTable.companyId.eq(userDetails.companyId))
            )
            .fetchOne(retrieveResponseTableMapper)
    }
}
