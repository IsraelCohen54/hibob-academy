package com.feedback.service

import com.feedback.dao.FeedbackDao
import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedFeedback
import org.springframework.stereotype.Service

@Service
class FeedbackFetcher(private val feedbackDao: FeedbackDao) {

    fun getFeedbackById(userDetails: LoggedInUser, feedbackId: Long): PersistedFeedback {
        return feedbackDao.getFeedbackById(userDetails, feedbackId)
            ?: throw IllegalStateException("No feedback was found with this feedbackId: $feedbackId.")
    }

    fun getUserFeedbacks(userDetails: LoggedInUser): List<PersistedFeedback> {
        return feedbackDao.getUserFeedbacks(userDetails)
            ?: throw IllegalStateException("No feedback was found for this user ID: ${userDetails.employeeId}.")
    }

    fun filterFeedback(userDetails: LoggedInUser, filters: List<FeedbackFilter>): Map<Long, String> {
        return feedbackDao.filterFeedback(userDetails, filters)
            ?: throw IllegalStateException(
                "No feedback was found for this user ID: ${userDetails.employeeId}" +
                        " and those filters, maybe you should try tweaking the filters or try changing the userId."
            )
    }
}
