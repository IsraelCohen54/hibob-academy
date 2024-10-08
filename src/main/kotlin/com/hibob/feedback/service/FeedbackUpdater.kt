package com.hibob.feedback.service

import com.hibob.feedback.dao.LoggedInUser
import com.hibob.feedback.dao.StatusType
import org.springframework.stereotype.Service

@Service
class FeedbackUpdater(private val feedbackDao: com.hibob.feedback.dao.FeedbackDao) {

    fun updateFeedbackStatus(userDetails: LoggedInUser, feedbackId: Long, newStatus: StatusType) {
        feedbackDao.updateFeedbackStatus(userDetails, feedbackId, newStatus)
    }
}
