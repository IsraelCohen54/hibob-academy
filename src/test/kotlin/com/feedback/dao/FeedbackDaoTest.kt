package com.feedback.dao

import com.feedback.service.AnonymousFilter
import com.feedback.service.DepartmentFilter
import com.feedback.service.FromDateFilter
import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import kotlin.random.Random

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val feedbackTable = FeedbackTable.instance
    private val companyId = Random.nextLong()
    private val feedbackDao = com.hibob.feedback.dao.FeedbackDao(sql)

    private val dummyEmployeeId = Random.nextLong()
    private val notExistFeedbackId = 999L
    private val dummyAnonymousFeedback = FeedbackCreationRequest(
        department = null,
        comment = "This is a test anonymous comment",
    )
    private val userDetails: LoggedInUser = LoggedInUser(companyId, dummyEmployeeId)

    private val dummyPublicFeedback = FeedbackCreationRequest(
        department = DepartmentType.PRODUCT,
        comment = "This is a test public comment",
    )

    @Test
    fun `test getFeedbackById returns correct feedback`() {

        val feedbackId = feedbackDao.insertFeedback(userDetails, dummyAnonymousFeedback)
        val feedback = feedbackDao.getFeedbackById(userDetails, feedbackId)
        assertEquals(feedbackId, feedback?.id)
        assertEquals(dummyAnonymousFeedback.comment, feedback?.comment)
    }

    @Test
    fun `test getFeedbackById returns null when feedback not found`() {
        val feedback = feedbackDao.getFeedbackById(userDetails, notExistFeedbackId)
        assertNull(feedback)
    }

    @Test
    fun `test getUserFeedbacksId returns correct list`() {
        val id1 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback.copy(comment = "some other comment"))
        feedbackDao.insertFeedback(userDetails.copy(employeeId = dummyEmployeeId - 1), dummyAnonymousFeedback)

        val result = feedbackDao.getUserFeedbacks(userDetails)

        val expected = listOf(id1, id2) //idx is unique
        result?.let {
            assertEquals(expected, result.map { it.id })
        }
    }

    @Test
    fun `test updateFeedbackStatus updates status correctly`() {
        val feedbackId = feedbackDao.insertFeedback(userDetails, dummyAnonymousFeedback)

        val updatedStatus = StatusType.SOLVED
        feedbackDao.updateFeedbackStatus(userDetails, feedbackId, updatedStatus)

        val updatedFeedback = feedbackDao.getFeedbackById(userDetails, feedbackId)
        assertEquals(updatedStatus, updatedFeedback?.status)
    }


    //            ~~~~~~~~~ 6 filter tests ~~~~~~~~~

    @Test
    fun `test filterFeedback applies no filters and returns all feedback`() {
        val id1 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(userDetails, dummyAnonymousFeedback)

        val result = feedbackDao.filterFeedback(userDetails, emptyList())

        val expected = mapOf(
            id1 to dummyPublicFeedback.comment,
            id2 to dummyAnonymousFeedback.comment
        )

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies anonymous filter (unknown employee Id)`() {
        feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(userDetails.copy(employeeId=null), dummyAnonymousFeedback)

        val filters = listOf(AnonymousFilter(true))
        val result = feedbackDao.filterFeedback(userDetails, filters)

        val expected = mapOf(id2 to dummyAnonymousFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies non-anonymous filter (known employee Id)`() {
        val id1 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        feedbackDao.insertFeedback(userDetails.copy(employeeId=null), dummyAnonymousFeedback)

        val filters = listOf(AnonymousFilter(false))
        val result = feedbackDao.filterFeedback(userDetails, filters)

        val expected = mapOf(id1 to dummyPublicFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies department filter`() {
        val filters = listOf(DepartmentFilter(DepartmentType.PRODUCT))

        val id1 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        feedbackDao.insertFeedback(userDetails, dummyAnonymousFeedback.copy(department = DepartmentType.IT))

        val result = feedbackDao.filterFeedback(userDetails, filters)

        val expected = mapOf(id1 to dummyPublicFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies multiple filters`() {
        val id1 = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)
        feedbackDao.insertFeedback(userDetails.copy(employeeId=null), dummyAnonymousFeedback.copy(department = DepartmentType.PRODUCT))
        feedbackDao.insertFeedback(userDetails, dummyPublicFeedback.copy(department = DepartmentType.IT, comment = "not anonymous but not PRODUCT department"))

        val filters = listOf(AnonymousFilter(false), DepartmentFilter(DepartmentType.PRODUCT))
        val result = feedbackDao.filterFeedback(userDetails, filters)

        val expected = mapOf(id1 to dummyPublicFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies FromDateFilter and returns correct feedback`() {
        val feedbackId = feedbackDao.insertFeedback(userDetails, dummyPublicFeedback)

        Thread.sleep(1000)
        val currentTime = Timestamp(System.currentTimeMillis())

        val filtersFromCurrentTime = listOf(FromDateFilter(currentTime))
        val resultFromCurrentTime = feedbackDao.filterFeedback(userDetails, filtersFromCurrentTime)

        assertEquals(emptyMap<Long, String>(), resultFromCurrentTime)

        val oneMinuteAgo = Timestamp(currentTime.time - 60 * 1000)
        val filtersFromOneMinuteAgo = listOf(FromDateFilter(oneMinuteAgo))
        val resultFromOneMinuteAgo = feedbackDao.filterFeedback(userDetails, filtersFromOneMinuteAgo)

        val expected = mapOf(feedbackId to dummyPublicFeedback.comment)
        assertEquals(expected, resultFromOneMinuteAgo)
    }



    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .execute()
    }
}
