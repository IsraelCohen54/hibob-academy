package com.feedback.service

import com.feedback.dao.DepartmentType
import com.feedback.dao.FeedbackCreationRequest
import com.feedback.dao.FeedbackDao
import com.feedback.dao.LoggedInUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class FeedbackInsertionServiceTest {

    private val feedbackDao: FeedbackDao = mock {}
    private val feedbackInsertion = FeedbackInsertion(feedbackDao)
    private val companyId = Random.nextLong()

    private val dummyFeedbackRequest = FeedbackCreationRequest(DepartmentType.IT, "kotlin installation should be done ASAP for any new Bober employee")
    private val dummyUserDetails = LoggedInUser(companyId = companyId, employeeId = Random.nextLong())

    @Test
    fun `insertFeedback should return feedback ID when insertion is successful`() {
        val expectedId = Random.nextLong()

        whenever(feedbackDao.insertFeedback(dummyUserDetails, dummyFeedbackRequest)).thenReturn(expectedId)

        val result = feedbackInsertion.insertFeedback(dummyUserDetails, dummyFeedbackRequest)

        assertEquals(expectedId, result)

        verify(feedbackDao).insertFeedback(dummyUserDetails, dummyFeedbackRequest)
    }
}
