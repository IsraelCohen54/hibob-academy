package com.hibob.feedback.service

import com.hibob.feedback.dao.DepartmentType
import com.hibob.feedback.dao.FeedbackTable
import org.jooq.Record2
import org.jooq.SelectConditionStep
import java.sql.Timestamp

interface FeedbackFilter {
    fun apply(
        query: SelectConditionStep<Record2<Long, String>>,
        feedbackTable: FeedbackTable
    ): SelectConditionStep<Record2<Long, String>>
}

class FromDateFilter(private val fromDate: Timestamp) : FeedbackFilter {
    override fun apply(
        query: SelectConditionStep<Record2<Long, String>>,
        feedbackTable: FeedbackTable
    ): SelectConditionStep<Record2<Long, String>> {
        return query.and(feedbackTable.creationTimestamp.ge(fromDate))
    }
}

class DepartmentFilter(private val department: DepartmentType) : FeedbackFilter {
    override fun apply(
        query: SelectConditionStep<Record2<Long, String>>,
        feedbackTable: FeedbackTable
    ): SelectConditionStep<Record2<Long, String>> {
        return query.and(feedbackTable.department.eq(department.name))
    }
}

class AnonymousFilter(private val isAnonymous: Boolean) : FeedbackFilter {
    override fun apply(
        query: SelectConditionStep<Record2<Long, String>>,
        feedbackTable: FeedbackTable
    ): SelectConditionStep<Record2<Long, String>> {
        return query.and(
            if (isAnonymous) feedbackTable.employeeId.isNull else feedbackTable.employeeId.isNotNull
        )
    }
}


