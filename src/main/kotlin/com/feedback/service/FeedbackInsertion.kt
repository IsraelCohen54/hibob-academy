package com.feedback.service


import com.feedback.dao.FeedbackCreationRequest
import com.feedback.dao.FeedbackDao
import com.feedback.dao.LoggedInUser
import org.springframework.stereotype.Service

@Service
class FeedbackInsertion(private val feedbackDao: FeedbackDao) {

    fun insertFeedback(userDetails: LoggedInUser, feedback: FeedbackCreationRequest): Long {
        return feedbackDao.insertFeedback(userDetails, feedback)
    }
}
