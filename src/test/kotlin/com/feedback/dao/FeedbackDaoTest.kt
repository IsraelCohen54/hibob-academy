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
    private val feedbackDao = FeedbackDao(sql)

    // Test fields
    private val dummyEmployeeId = Random.nextLong()
    private val notExistFeedbackId = 999L
    private val dummyAnonymousFeedback = Feedback.InsertFeedback(
        companyId = companyId,
        department = null,
        comment = "This is a test anonymous comment",
        employeeId = null
    )

    private val dummyPublicFeedback = Feedback.InsertFeedback(
        companyId = companyId,
        department = DepartmentType.PRODUCT,
        comment = "This is a test public comment",
        employeeId = dummyEmployeeId
    )

    @Test
    fun `test getFeedbackById returns correct feedback`() {
        val feedbackId = feedbackDao.insertFeedback(dummyAnonymousFeedback)
        val feedback = feedbackDao.getFeedbackById(feedbackId, companyId)
        assertEquals(feedbackId, feedback?.id)
        assertEquals(dummyAnonymousFeedback.comment, feedback?.comment)
    }

    @Test
    fun `test getFeedbackById returns null when feedback not found`() {
        val feedback = feedbackDao.getFeedbackById(notExistFeedbackId, companyId)
        assertNull(feedback)
    }

    @Test
    fun `test getFeedbackStatusByEmployeeAndCompany returns correct map`() {
        val id1 = feedbackDao.insertFeedback(dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(dummyPublicFeedback.copy(comment = "some other comment"))
        feedbackDao.insertFeedback(dummyAnonymousFeedback)

        val result = feedbackDao.getFeedbackStatusByEmployeeAndCompany(dummyEmployeeId, companyId)

        val shouldEqualTo :Map<Long, String> = mapOf(
            id1 to "NOT_SOLVED",
            id2 to "NOT_SOLVED"
        )

        assertEquals(shouldEqualTo, result)
    }

    @Test
    fun `test updateFeedbackStatus updates status correctly`() {
        val feedbackId = feedbackDao.insertFeedback(dummyAnonymousFeedback)

        val updatedStatus = StatusType.SOLVED
        feedbackDao.updateFeedbackStatus(feedbackId, companyId, updatedStatus)

        val updatedFeedback = feedbackDao.getFeedbackById(feedbackId, companyId)
        assertEquals(updatedStatus, updatedFeedback?.status)
    }


    //            ~~~~~~~~~ 6 filter tests ~~~~~~~~~

    @Test
    fun `test filterFeedback applies no filters and returns all feedback`() {
        val id1 = feedbackDao.insertFeedback(dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(dummyAnonymousFeedback)

        val result = feedbackDao.filterFeedback(emptyList(), companyId)

        val expected = mapOf(
            id1 to dummyPublicFeedback.comment,
            id2 to dummyAnonymousFeedback.comment
        )

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies anonymous filter (unknown employee Id)`() {
        feedbackDao.insertFeedback(dummyPublicFeedback)
        val id2 = feedbackDao.insertFeedback(dummyAnonymousFeedback)

        val filters = listOf(AnonymousFilter(true))
        val result = feedbackDao.filterFeedback(filters, companyId)

        val expected = mapOf(id2 to dummyAnonymousFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies non-anonymous filter (known employee Id)`() {
        val id1 = feedbackDao.insertFeedback(dummyPublicFeedback)
        feedbackDao.insertFeedback(dummyAnonymousFeedback)

        val filters = listOf(AnonymousFilter(false))
        val result = feedbackDao.filterFeedback(filters, companyId)

        val expected = mapOf(id1 to dummyPublicFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies department filter`() {
        // Applying DepartmentFilter with "PRODUCT"
        val filters = listOf(DepartmentFilter(DepartmentType.PRODUCT.toString()))

        val id1 = feedbackDao.insertFeedback(dummyPublicFeedback) // Department is PRODUCT
        feedbackDao.insertFeedback(dummyAnonymousFeedback.copy(department = DepartmentType.IT))

        val result = feedbackDao.filterFeedback(filters, companyId)

        val expected = mapOf(id1 to dummyPublicFeedback.comment)

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies multiple filters`() {
        val id1 = feedbackDao.insertFeedback(dummyPublicFeedback)
        feedbackDao.insertFeedback(dummyAnonymousFeedback.copy(department = DepartmentType.PRODUCT))
        feedbackDao.insertFeedback(dummyPublicFeedback.copy(department = DepartmentType.IT, comment = "not anonymous but not PRODUCT department"))

        val filters = listOf(AnonymousFilter(false), DepartmentFilter(DepartmentType.PRODUCT.toString()))
        val result = feedbackDao.filterFeedback(filters, companyId)

        val expected = mapOf(id1 to dummyPublicFeedback.comment) // Only non-anonymous feedback from PRODUCT department

        assertEquals(expected, result)
    }

    @Test
    fun `test filterFeedback applies FromDateFilter and returns correct feedback`() {
        val feedbackId = feedbackDao.insertFeedback(dummyPublicFeedback)

        Thread.sleep(1000)
        val currentTime = Timestamp(System.currentTimeMillis())

        val filtersFromCurrentTime = listOf(FromDateFilter(currentTime))
        val resultFromCurrentTime = feedbackDao.filterFeedback(filtersFromCurrentTime, companyId)

        // Assert no feedback is returned filtering from current time
        assertEquals(emptyMap<Long, String>(), resultFromCurrentTime)

        // validate filter from 1 minute earlier get the inserted feedback:
        val oneMinuteAgo = Timestamp(currentTime.time - 60 * 1000)
        val filtersFromOneMinuteAgo = listOf(FromDateFilter(oneMinuteAgo))
        val resultFromOneMinuteAgo = feedbackDao.filterFeedback(filtersFromOneMinuteAgo, companyId)

        val expected = mapOf(feedbackId to dummyPublicFeedback.comment)
        assertEquals(expected, resultFromOneMinuteAgo)
    }



    @BeforeEach
    @AfterEach
    fun cleanup() {
        // Clean up any feedback related to this company ID
        sql.deleteFrom(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .execute()
    }
}
