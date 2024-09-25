package com.feedback.service

import com.feedback.dao.LoggedInUser
import com.feedback.dao.ResponseCreationRequest
import com.feedback.dao.ResponseDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class ForceResponseInsertionServiceTest {

    private val responseDao: ResponseDao = mock {}
    private val insertResponse = ForceResponseInsertion(responseDao)
    private val companyId = Random.nextLong()

    private val feedbackId = Random.nextLong()
    private val responderId = Random.nextLong()
    private val dummyUserDetails = LoggedInUser(companyId = companyId, employeeId = responderId)
    private val dummyResponseCreationRequest = ResponseCreationRequest(feedbackId, "Well done! The Dao layer looks amazing, but you have to use logger as well", responderId)

    @Test
    fun `createOrUpdateResponse should return response ID when insert is successful`() {
        val expectedId = Random.nextLong()

        whenever(responseDao.forceInsertResponse(dummyUserDetails, dummyResponseCreationRequest))
            .thenReturn(expectedId)

        val result = insertResponse.createOrUpdateResponse(dummyUserDetails, dummyResponseCreationRequest)

        assertEquals(expectedId, result)
        verify(responseDao).forceInsertResponse(dummyUserDetails, dummyResponseCreationRequest)
    }
}
