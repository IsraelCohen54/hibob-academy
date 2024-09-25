package com.feedback.service

import com.feedback.dao.FeedbackDao
import com.feedback.dao.LoggedInUser
import com.feedback.dao.StatusType
import org.springframework.stereotype.Service

@Service
class FeedbackUpdate(private val feedbackDao: FeedbackDao) {

    fun updateFeedbackStatus(userDetails: LoggedInUser, feedbackId: Long, newStatus: StatusType) {
        feedbackDao.updateFeedbackStatus(userDetails, feedbackId, newStatus)
    }
}
