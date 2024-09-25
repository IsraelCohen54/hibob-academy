package com.feedback.service

import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedResponse
import com.feedback.dao.ResponseDao
import org.springframework.stereotype.Service

@Service
class FetchResponse(private val responseDao: ResponseDao) {

    fun getResponseDetails(userDetails: LoggedInUser, feedbackId: Long): PersistedResponse {
        return responseDao.getResponseByFeedbackId(userDetails, feedbackId)
            ?: throw IllegalStateException("No response found for feedback ID $feedbackId")
    }
}
