package com.feedback.service

import com.feedback.dao.FeedbackDao
import com.feedback.dao.LoggedInUser
import com.feedback.dao.StatusType
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.random.Random

class FeedbackUpdaterServiceTest {

    private val feedbackDao: FeedbackDao = mock()
    private val feedbackUpdaterService = FeedbackUpdater(feedbackDao)
    private val companyId = Random.nextLong()

    private val dummyFeedbackId = Random.nextLong()
    private val dummyNewStatus = StatusType.SOLVED
    private val dummyUserDetails = LoggedInUser(companyId = companyId, employeeId = Random.nextLong())

    @Test
    fun `updateFeedbackStatus should call DAO update method with correct parameters`() {
        feedbackUpdaterService.updateFeedbackStatus(dummyUserDetails, dummyFeedbackId, dummyNewStatus)

        verify(feedbackDao).updateFeedbackStatus(dummyUserDetails, dummyFeedbackId, dummyNewStatus)
    }
}
