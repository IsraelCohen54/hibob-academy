package com.feedback.dao

import com.feedback.service.FeedbackFilter
import org.jooq.*
import org.springframework.stereotype.Component

@Component
class FeedbackDao(private val sql: DSLContext) {

    private val feedbackTable = FeedbackTable.instance

    private val retrieveFeedbackTableMapper = RecordMapper<Record, Feedback.RetrieveFeedback> { record ->
        Feedback.RetrieveFeedback(
            id = record[feedbackTable.id],
            companyId = record[feedbackTable.companyId],
            department = record[feedbackTable.department]?.let { DepartmentType.fromString(it) },
            comment = record[feedbackTable.comment],
            creationTimestamp = record[feedbackTable.creationTimestamp],
            status = record[feedbackTable.status]?.let  { StatusType.fromString(it) } ?: StatusType.NOT_SOLVED, //todo ask why can be nullable
            employeeId = record[feedbackTable.employeeId]
        )
    }

    fun insertFeedback(feedback: Feedback.InsertFeedback): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, feedback.companyId)
            .set(feedbackTable.department, feedback.department?.toString())
            .set(feedbackTable.comment, feedback.comment)
            .set(feedbackTable.employeeId, feedback.employeeId)
            .returning(feedbackTable.id)
            .fetchOne()
            ?.get(feedbackTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated feedback ID.")
    }

    fun getFeedbackById(feedbackId: Long, companyId: Long): Feedback.RetrieveFeedback? {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId)
                .and(feedbackTable.companyId.eq(companyId)))
            .fetchOne(retrieveFeedbackTableMapper)
    }


    fun filterFeedback(filters: List<FeedbackFilter>, companyId: Long): Map<Long, String>? {
        var query: SelectConditionStep<Record2<Long, String>> = sql.select(feedbackTable.id, feedbackTable.comment)
            .from(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))

        filters.forEach { filter ->
            query = filter.apply(query, feedbackTable)
        }

        return query.fetch().associate { record ->
            record[feedbackTable.id] to record[feedbackTable.comment]
        }
    }


    fun getFeedbackStatusByEmployeeAndCompany(employeeId: Long, companyId: Long): Map<Long, String> {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.employeeId.eq(employeeId))
            .and(feedbackTable.companyId.eq(companyId))
            .fetch(retrieveFeedbackTableMapper) // Fetch full objects via mapper then choose wanted columns
            .associate { feedback -> feedback.id to feedback.status.toString() }
    }

    fun updateFeedbackStatus(feedbackId: Long, companyId: Long, newStatus: StatusType) {
        sql.update(feedbackTable)
            .set(feedbackTable.status, newStatus.toString())
            .where(feedbackTable.id.eq(feedbackId))
            .and(feedbackTable.companyId.eq(companyId))
            .execute()
    }
}


