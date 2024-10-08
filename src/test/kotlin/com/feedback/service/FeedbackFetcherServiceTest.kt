package com.feedback.service

import com.feedback.dao.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.Instant
import kotlin.random.Random

class FeedbackFetcherServiceTest {

    private val feedbackDao: com.hibob.feedback.dao.FeedbackDao = mock {}
    private val feedbackService = FeedbackFetcher(feedbackDao)
    private val companyId = Random.nextLong()
    private val feedbackId = Random.nextLong()

    private val dummyUserDetails = LoggedInUser(companyId = companyId, employeeId = Random.nextLong())
    private val dummyPersistedFeedback = PersistedFeedback(
        feedbackId,
        DepartmentType.CX,
        "Great, everything is neat and tidy, nothing is broken",
        Timestamp.from(Instant.now()),
        StatusType.PAUSED,
        dummyUserDetails.employeeId
    )


    @Test
    fun `getFeedbackById should return feedback when found`() {
        whenever(feedbackDao.getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)).thenReturn(
            dummyPersistedFeedback
        )

        val result = feedbackService.getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)

        assertEquals(dummyPersistedFeedback, result)
        verify(feedbackDao).getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)
    }

    @Test
    fun `getFeedbackById should throw exception when feedback is not found`() {
        whenever(feedbackDao.getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)).thenReturn(null)

        assertEquals(
            "No feedback was found with this feedbackId: ${dummyPersistedFeedback.id}.",
            assertThrows<IllegalStateException> {
                feedbackService.getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)
            }.message
        )

        verify(feedbackDao).getFeedbackById(dummyUserDetails, dummyPersistedFeedback.id)
    }

    @Test
    fun `getUserFeedbacks should return feedback list when found`() {
        whenever(feedbackDao.getUserFeedbacks(dummyUserDetails)).thenReturn(listOf(dummyPersistedFeedback))

        val result = feedbackService.getUserFeedbacks(dummyUserDetails)

        assertEquals(listOf(dummyPersistedFeedback), result)
        verify(feedbackDao).getUserFeedbacks(dummyUserDetails)
    }

    @Test
    fun `getUserFeedbacks should throw exception when no feedback is found`() {
        whenever(feedbackDao.getUserFeedbacks(dummyUserDetails)).thenReturn(null)

        assertEquals(
            "No feedback was found for this user ID: ${dummyUserDetails.employeeId}.",
            assertThrows<IllegalStateException> {
                feedbackService.getUserFeedbacks(dummyUserDetails)
            }.message
        )

        verify(feedbackDao).getUserFeedbacks(dummyUserDetails)
    }

    @Test
    fun `filterFeedback should return feedback map when found`() {
        val filters = listOf(
            DepartmentFilter(DepartmentType.IT),
            FromDateFilter(Timestamp.valueOf("2024-01-01 00:00:00"))
        )
        val expectedFeedback = mapOf(1L to "Amazing", 2L to "Supreme")

        whenever(feedbackDao.filterFeedback(dummyUserDetails, filters)).thenReturn(expectedFeedback)

        val result = feedbackService.filterFeedback(dummyUserDetails, filters)

        assertEquals(expectedFeedback, result)

        verify(feedbackDao).filterFeedback(dummyUserDetails, filters)
    }

    @Test
    fun `filterFeedback should throw IllegalStateException when no feedback is found`() {
        val filters = listOf(DepartmentFilter(DepartmentType.IT))

        whenever(feedbackDao.filterFeedback(dummyUserDetails, filters)).thenReturn(null)

        assertEquals(
            "No feedback was found for this user ID: ${dummyUserDetails.employeeId} and those filters," +
                    " maybe you should try tweaking the filters or try changing the userId.",
            assertThrows<IllegalStateException> {
                feedbackService.filterFeedback(dummyUserDetails, filters)
            }.message
        )

        verify(feedbackDao).filterFeedback(dummyUserDetails, filters)
    }

}
