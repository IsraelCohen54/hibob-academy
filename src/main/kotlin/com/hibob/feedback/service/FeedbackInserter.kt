package com.hibob.feedback.service


import com.hibob.feedback.dao.FeedbackCreationRequest
import com.hibob.feedback.dao.LoggedInUser
import org.springframework.stereotype.Service

@Service
class FeedbackInserter(private val feedbackDao: com.hibob.feedback.dao.FeedbackDao) {

    fun insertFeedback(userDetails: LoggedInUser, feedback: FeedbackCreationRequest): Long {
        return feedbackDao.insertFeedback(userDetails, feedback)
    }
}
