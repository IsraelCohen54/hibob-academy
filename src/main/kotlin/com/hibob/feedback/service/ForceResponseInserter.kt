package com.hibob.feedback.service

import com.hibob.feedback.dao.LoggedInUser
import com.hibob.feedback.dao.ResponseCreationRequest
import com.hibob.feedback.dao.ResponseDao
import org.springframework.stereotype.Service

@Service
class ForceResponseInserter(private val responseDao: ResponseDao) {

    fun createOrUpdateResponse(userDetails: LoggedInUser, responseRequest: ResponseCreationRequest): Long {
        return responseDao.forceInsertResponse(userDetails, responseRequest)
    }
}
