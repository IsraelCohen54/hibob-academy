package com.feedback.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class ResponseDao(private val sql: DSLContext) {

    private val responseTable = ResponseTable.instance

    /*
    private val responseTableMapper = RecordMapper<Record, Response> { record ->
        Response(
            id = record[responseTable.id],
            companyId = record[responseTable.companyId],
            feedbackId = record[responseTable.feedbackId],
            response = record[responseTable.response]
        )
    }

    fun insertResponse(response: Response): Long {
        return sql.insertInto(responseTable)
            .set(responseTable.companyId, response.companyId)
            .set(responseTable.feedbackId, response.feedbackId)
            .set(responseTable.response, response.response)
            .returning(responseTable.id)
            .fetchOne()
            ?.get(responseTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated response ID.")
    }

    fun getResponseByFeedbackId(feedbackId: Long, companyId: Long): Response? {
        return sql.selectFrom(responseTable)
            .where(responseTable.feedbackId.eq(feedbackId)
                .and(responseTable.companyId.eq(companyId)))
            .fetchOne(responseTableMapper)
    }

    fun getResponsesByFeedback(feedbackId: Long, companyId: Long): List<Response> {
        return sql.selectFrom(responseTable)
            .where(responseTable.feedbackId.eq(feedbackId)
                .and(responseTable.companyId.eq(companyId)))
            .fetch(responseTableMapper)
    }

    fun updateResponse(responseId: Long, companyId: Long, newResponse: String) {
        sql.update(responseTable)
            .set(responseTable.response, newResponse)
            .where(responseTable.id.eq(responseId))
            .and(responseTable.companyId.eq(companyId))
            .execute()
    }

    // Delete a response
    fun deleteResponse(responseId: Long, companyId: Long) {
        sql.deleteFrom(responseTable)
            .where(responseTable.id.eq(responseId)
                .and(responseTable.companyId.eq(companyId)))
            .execute()
    }
    */

}
