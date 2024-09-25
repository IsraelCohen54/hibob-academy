package com.feedback.service

import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedResponse
import com.feedback.dao.ResponseDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class ResponseFetcherServiceTest {

    private val responseDao: ResponseDao = mock {}
    private val responseFetcher = ResponseFetcher(responseDao)
    private val companyId = Random.nextLong()

    private val feedbackId = Random.nextLong()
    private val responderId = Random.nextLong()
    private val dummyUserDetails = LoggedInUser(companyId = companyId, employeeId = responderId)

    private val dummyPersistedResponse = PersistedResponse(
        id = Random.nextLong(),
        feedbackId = feedbackId,
        response = "Great work can be done only while being highly awake," +
                "coffee could help as well, thank for the coffee!",
        responderId = responderId
    )

    @Test
    fun `getResponseDetails should return response when found`() {
        whenever(responseDao.getResponseByFeedbackId(dummyUserDetails, feedbackId))
            .thenReturn(dummyPersistedResponse)

        val result = responseFetcher.getResponseDetails(dummyUserDetails, feedbackId)

        assertEquals(dummyPersistedResponse, result)
        verify(responseDao).getResponseByFeedbackId(dummyUserDetails, feedbackId)
    }

    @Test
    fun `getResponseDetails should throw exception when no response is found`() {
        whenever(responseDao.getResponseByFeedbackId(dummyUserDetails, feedbackId))
            .thenReturn(null)

        assertEquals(
            "No response found for feedback ID $feedbackId",
            assertThrows<IllegalStateException> {
                responseFetcher.getResponseDetails(dummyUserDetails, feedbackId)
            }.message
        )

        verify(responseDao).getResponseByFeedbackId(dummyUserDetails, feedbackId)
    }
}
