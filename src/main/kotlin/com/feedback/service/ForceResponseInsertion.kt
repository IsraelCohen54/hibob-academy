package com.feedback.service

import com.feedback.dao.LoggedInUser
import com.feedback.dao.ResponseCreationRequest
import com.feedback.dao.ResponseDao
import org.springframework.stereotype.Service

@Service
class ForceResponseInsertion(private val responseDao: ResponseDao) {

    fun createOrUpdateResponse(userDetails: LoggedInUser, responseRequest: ResponseCreationRequest): Long {
        return responseDao.forceInsertResponse(userDetails, responseRequest)
    }
}
