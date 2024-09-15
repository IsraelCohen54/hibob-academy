package com.hibob

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class UserServiceTest {


    private val userDao: UserDao = mock<UserDao>{}
    private val notificationService: NotificationService = mock<NotificationService>{}
    private val emailVerificationService: EmailVerificationService = mock<EmailVerificationService>{}
    private val userService = UserService(userDao, notificationService, emailVerificationService)

    @Test
    fun `registerUser should throw if user id exist`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)

        whenever(userDao.findById(user.id)).thenReturn(user)
        assertThrows<IllegalArgumentException>("User already exists") { userService.registerUser(user) }
    }

    @Test
    fun `registerUser - registerUser throw if email verified`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever(userDao.save(user.copy(isEmailVerified = false))).thenReturn(false)
        assertThrows<IllegalStateException>("\"User registration failed\"") { userService.registerUser(user)}
    }

    @Test
    fun `registerUser - isVerificationEmailSent throw if email failed to be send`() {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever(userDao.save(user.copy(isEmailVerified = false))).thenReturn(true)
        whenever(emailVerificationService.sendVerificationEmail(user.email)).thenReturn(false)
        assertThrows<IllegalStateException>("Failed to send verification email") { userService.registerUser(user)}
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
        assertThrows<IllegalArgumentException>("User not found") { userService.verifyUserEmail(0, "abc") }
    }

    @Test
    fun `verifyUserEmail - isEmailVerified throw if email not verified` () {
        val user = User(1, "a", "a@a.co", "1q2w3e", false)
        val token = "abc"

        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(user.email, token)).thenReturn(false)
        assertThrows<IllegalArgumentException>("Email verification failed") { userService.verifyUserEmail(1, token) }
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