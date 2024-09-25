package com.feedback.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.random.Random

@BobDbTest
class ResponseDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val responseTable = ResponseTable.instance
    private val companyId = Random.nextLong()
    private val responseDao = ResponseDao(sql)

    // Test fields
    private val dummyKnownUserDetails: LoggedInUser = LoggedInUser(companyId, Random.nextLong())
    private val dummyFeedbackId: Long = Random.nextLong()
    private val dummyResponse: String = "funny migration name: V2024092423:01__, a bit late apparently"
    private val dummyId : Long = Random.nextLong()

    private val dummyResponseCreation = ResponseCreationRequest(dummyFeedbackId, dummyResponse, dummyKnownUserDetails.employeeId!!)
    private val dummyRetrievedResponse = PersistedResponse(dummyId,dummyFeedbackId, dummyResponse, dummyKnownUserDetails.employeeId!!)



    @Test
    fun `test forceInsertResponse inserts new response`() {

        val responseId = responseDao.forceInsertResponse(dummyKnownUserDetails, dummyResponseCreation)

        val result = responseDao.getResponseByFeedbackId(dummyKnownUserDetails, dummyFeedbackId)

        // Assertions
        assertNotNull(result, "Response should not be null after insertion")
        assertEquals(responseId, result?.id, "Inserted response ID should match")
        assertEquals(dummyResponseCreation.response, result?.response, "Inserted response content should match")
        assertEquals(dummyResponseCreation.responderId, result?.responderId, "Inserted responder ID should match")
    }

    @Test
    fun `test forceInsertResponse updates existing response on conflict`() {

        responseDao.forceInsertResponse(dummyKnownUserDetails, dummyResponseCreation)

        val updatedInsertResponse = dummyResponseCreation.copy(response = "new response, same personal, same feedback id")
        val updatedResponseId = responseDao.forceInsertResponse(dummyKnownUserDetails, updatedInsertResponse)

        val result = responseDao.getResponseByFeedbackId(dummyKnownUserDetails, dummyFeedbackId)

        assertNotNull(result, "Response should not be null after update")
        assertEquals(updatedResponseId, result?.id, "Updated response ID should match")
        assertEquals(updatedInsertResponse.response, result?.response, "Updated response content should match")
    }

    @Test
    fun `test getResponseByFeedbackId retrieves correct response`() {

        val responseId = responseDao.forceInsertResponse(dummyKnownUserDetails, dummyResponseCreation)

        val result = responseDao.getResponseByFeedbackId(dummyKnownUserDetails, dummyFeedbackId)

        assertEquals(result, dummyRetrievedResponse.copy(id = responseId), "Response content should match")
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(responseTable).where(responseTable.companyId.eq(companyId)).execute()
    }
}