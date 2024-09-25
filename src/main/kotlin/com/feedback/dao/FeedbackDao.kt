package com.feedback.dao

import com.feedback.service.FeedbackFilter
import org.jooq.*
import org.springframework.stereotype.Component

@Component
class FeedbackDao(private val sql: DSLContext) {

    private val feedbackTable = FeedbackTable.instance

    private val retrieveFeedbackTableMapper = RecordMapper<Record, RetrieveFeedbackRequest> { record ->
        RetrieveFeedbackRequest(
            id = record[feedbackTable.id],
            department = record[feedbackTable.department]?.let { DepartmentType.fromString(it) },
            comment = record[feedbackTable.comment],
            creationTimestamp = record[feedbackTable.creationTimestamp],
            status = record[feedbackTable.status]?.let { StatusType.fromString(it) } ?: StatusType.NOT_SOLVED,
            employeeId = record[feedbackTable.employeeId]
        )
    }

    fun insertFeedback(userDetails: LoggedInUser, feedback: FeedbackCreationRequest): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, userDetails.companyId)
            .set(feedbackTable.department, feedback.department?.toString())
            .set(feedbackTable.comment, feedback.comment)
            .set(feedbackTable.employeeId, userDetails.employeeId)
            .returning(feedbackTable.id)
            .fetchOne()
            ?.get(feedbackTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated feedback ID.")
    }

    fun getFeedbackById(userDetails: LoggedInUser, feedbackId: Long): RetrieveFeedbackRequest? {
        return sql.selectFrom(feedbackTable)
            .where(
                feedbackTable.id.eq(feedbackId)
                    .and(feedbackTable.companyId.eq(userDetails.companyId))
            )
            .fetchOne(retrieveFeedbackTableMapper)
    }


    fun filterFeedback(userDetails: LoggedInUser, filters: List<FeedbackFilter>): Map<Long, String>? {
        var query: SelectConditionStep<Record2<Long, String>> = sql.select(feedbackTable.id, feedbackTable.comment)
            .from(feedbackTable)
            .where(feedbackTable.companyId.eq(userDetails.companyId))

        filters.forEach { filter ->
            query = filter.apply(query, feedbackTable)
        }

        return query.fetch().associate { record ->
            record[feedbackTable.id] to record[feedbackTable.comment]
        }
    }


    fun getUserFeedbacks(userDetails: LoggedInUser): List<RetrieveFeedbackRequest>? {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.employeeId.eq(userDetails.employeeId))
            .and(feedbackTable.companyId.eq(userDetails.companyId))
            .fetch(retrieveFeedbackTableMapper)
    }

    fun updateFeedbackStatus(userDetails: LoggedInUser, feedbackId: Long, newStatus: StatusType) {
        sql.update(feedbackTable)
            .set(feedbackTable.status, newStatus.toString())
            .where(feedbackTable.id.eq(feedbackId))
            .and(feedbackTable.companyId.eq(userDetails.companyId))
            .execute()
    }
}


