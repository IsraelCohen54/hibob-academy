package com.hibob.feedback.dao

import com.hibob.feedback.service.FeedbackFilter
import org.jooq.*
import org.springframework.stereotype.Component

@Component
class FeedbackDao(private val sql: DSLContext) {

    private val feedbackTable = com.hibob.feedback.dao.FeedbackTable.Companion.instance

    private val retrieveFeedbackTableMapper = RecordMapper<Record, com.hibob.feedback.dao.PersistedFeedback> { record ->
        com.hibob.feedback.dao.PersistedFeedback(
            id = record[feedbackTable.id],
            department = record[feedbackTable.department]?.let {
                com.hibob.feedback.dao.DepartmentType.Companion.fromString(
                    it
                )
            },
            comment = record[feedbackTable.comment],
            creationTimestamp = record[feedbackTable.creationTimestamp],
            status = com.hibob.feedback.dao.StatusType.Companion.fromString(record[feedbackTable.status]),
            employeeId = record[feedbackTable.employeeId]
        )
    }

    fun insertFeedback(userDetails: com.hibob.feedback.dao.LoggedInUser, feedback: com.hibob.feedback.dao.FeedbackCreationRequest): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, userDetails.companyId)
            .set(feedbackTable.department, feedback.department?.name)
            .set(feedbackTable.comment, feedback.comment)
            .set(feedbackTable.employeeId, userDetails.employeeId)
            .returning(feedbackTable.id)
            .fetchOne()
            ?.get(feedbackTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated feedback ID.")
    }

    fun getFeedbackById(userDetails: com.hibob.feedback.dao.LoggedInUser, feedbackId: Long): com.hibob.feedback.dao.PersistedFeedback? {
        return sql.selectFrom(feedbackTable)
            .where(
                feedbackTable.id.eq(feedbackId)
                    .and(feedbackTable.companyId.eq(userDetails.companyId))
            )
            .fetchOne(retrieveFeedbackTableMapper)
    }


    fun filterFeedback(userDetails: com.hibob.feedback.dao.LoggedInUser, filters: List<FeedbackFilter>): Map<Long, String>? {
        val initialQuery: SelectConditionStep<Record2<Long, String>> = sql.select(feedbackTable.id, feedbackTable.comment)
            .from(feedbackTable)
            .where(feedbackTable.companyId.eq(userDetails.companyId))

        val finalQuery = filters.fold(initialQuery) { acc, filter ->
            filter.apply(acc, feedbackTable)
        }

        return finalQuery.fetch().associate { record ->
            record[feedbackTable.id] to record[feedbackTable.comment]
        }
    }


    fun getUserFeedbacks(userDetails: com.hibob.feedback.dao.LoggedInUser): List<com.hibob.feedback.dao.PersistedFeedback>? {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.employeeId.eq(userDetails.employeeId))
            .and(feedbackTable.companyId.eq(userDetails.companyId))
            .fetch(retrieveFeedbackTableMapper)
    }

    fun updateFeedbackStatus(userDetails: com.hibob.feedback.dao.LoggedInUser, feedbackId: Long, newStatus: com.hibob.feedback.dao.StatusType) {
        sql.update(feedbackTable)
            .set(feedbackTable.status, newStatus.name)
            .where(feedbackTable.id.eq(feedbackId))
            .and(feedbackTable.companyId.eq(userDetails.companyId))
            .execute()
    }
}


