package com.hibob.feedback.service

import com.hibob.feedback.dao.LoggedInUser
import com.hibob.feedback.dao.PersistedResponse
import com.hibob.feedback.dao.ResponseDao
import org.springframework.stereotype.Service

@Service
class ResponseFetcher(private val responseDao: ResponseDao) {

    fun getResponseDetails(userDetails: LoggedInUser, feedbackId: Long): PersistedResponse {
        return responseDao.getResponseByFeedbackId(userDetails, feedbackId)
            ?: throw IllegalStateException("No response found for feedback ID $feedbackId")
    }
}
