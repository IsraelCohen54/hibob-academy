package com.feedback.resource

import com.feedback.dao.FeedbackCreationRequest
import com.feedback.dao.LoggedInUser
import com.feedback.service.*
import org.springframework.stereotype.Component

@Component
class RequestPreparetor(private val employeeFetcher: EmployeeFetcher)
{
class RequestPreparetor(
    private val employeeFetcher: EmployeeFetcher,
) {
class RequestPreparetor(private val employeeFetcher: EmployeeFetcher)
{
    fun prepareRequestWithAnonymity(loggedInUser: LoggedInUser, isAnonymous: Boolean, comment: String) =
        if (isAnonymous) FeedbackCreationRequest(department = null, comment = comment)
        else FeedbackCreationRequest(
            department = employeeFetcher.getEmployeeDetails(loggedInUser).department,
            comment = comment
        )

    fun prepareViewWithFilterRequest(filters: FilterFeedbackRequest): List<FeedbackFilter> {
        return listOfNotNull(
            filters.timestamp?.let { FromDateFilter(it) },
            filters.department?.let { DepartmentFilter(it) },
            filters.isAnonymous?.let { AnonymousFilter(it) }
        )
    }

}