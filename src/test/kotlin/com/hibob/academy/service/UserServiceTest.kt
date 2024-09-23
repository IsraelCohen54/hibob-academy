package com.hibob.academy.service

import com.hibob.kotlin_exercises.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserServiceTest {


    private val userDao: UserDao = mock<UserDao>{}
    private val notificationService: NotificationService = mock<NotificationService>{}
    private val emailVerificationService: EmailVerificationService = mock<EmailVerificationService>{}
    private val userService = UserService(userDao, notificationService, emailVerificationService)

    @Test
    fun `registerUser should throw if user id exist`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)

        whenever(userDao.findById(user.id)).thenReturn(user)
        val exception = assertThrows<IllegalArgumentException> { userService.registerUser(user) }
        assertEquals("User already exists", exception.message)
    }

    @Test
    fun `registerUser - registerUser throw if email verified`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever(userDao.save(user.copy(isEmailVerified = false))).thenReturn(false)

        val exception = assertThrows<IllegalStateException> { userService.registerUser(user) }
        assertEquals("User registration failed", exception.message)
    }

    @Test
    fun `registerUser - isVerificationEmailSent throw if email failed to be send`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever(userDao.save(user.copy(isEmailVerified = false))).thenReturn(true)
        whenever(emailVerificationService.sendVerificationEmail(user.email)).thenReturn(false)

        val exception = assertThrows<IllegalStateException> { userService.registerUser(user) }
        assertEquals("Failed to send verification email", exception.message)
    }

    @Test
    fun `registerUser - return true if passed all`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever(userDao.save(user.copy(isEmailVerified = false))).thenReturn(true)
        whenever(emailVerificationService.sendVerificationEmail(user.email)).thenReturn(true)
        assert(userService.registerUser(user))
    }

    @Test
    fun `verifyUserEmail - isEmailVerified throw if id not found` () {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)

        whenever(userDao.findById(user.id)).thenReturn(user)
        val exception = assertThrows<IllegalArgumentException> { userService.verifyUserEmail(0, "abc") }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `verifyUserEmail - isEmailVerified throw if email not verified` () {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        val token = "abc"

        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(user.email, token)).thenReturn(false)
        val exception = assertThrows<IllegalArgumentException> { userService.verifyUserEmail(1, token) }
        assertEquals("Email verification failed", exception.message)
    }

    @Test
    fun `verifyUserEmail - isUpdated true - send email` () {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        val token = "abc"

        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(user.email, token)).thenReturn(true)

        val updatedUser = user.copy(isEmailVerified = true)

        whenever(userDao.update(updatedUser)).thenReturn(true)
        assert(userService.verifyUserEmail(1, token))
        verify(notificationService).sendEmail(user.email, "Welcome ${user.name}!")

    }

    @Test
    fun `verifyUserEmail - isUpdated false then throw` () {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        val token = "abc"

        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(user.email, token)).thenReturn(true)

        val updatedUser = user.copy(isEmailVerified = true)

        whenever(userDao.update(updatedUser)).thenReturn(false)
        assertFalse(userService.verifyUserEmail(1, token))
        verify(notificationService, never()).sendEmail(user.email, "Welcome ${user.name}!")

    }
}