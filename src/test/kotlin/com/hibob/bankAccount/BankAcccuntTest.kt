package com.hibob.bankAccount
import com.hibob.bootcamp.BankAccount
import org.junit.jupiter.api.Test


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * Write unit tests to verify that the deposit and withdraw methods function correctly.
 * Handle edge cases, such as invalid inputs (e.g., negative amounts).
 * Ensure that the getBalance method returns the correct balance after a series of deposits and withdrawals.
 */

class BankAccountTest {

    @Test
    fun `deposit valid amount increases balance`() {
        val ba = BankAccount(23.5)
        assertEquals(47.0, ba.deposit(23.5))
    }

    @Test
    fun `deposit negative or zero amount throws IllegalArgumentException`() {
        val ba = BankAccount(23.5)
        assertThrows<IllegalArgumentException> { ba.deposit(-2.2) }
    }

    @Test
    fun `withdraw valid amount decreases balance`() {
        val ba = BankAccount(23.5)
        assertEquals(0.0, ba.withdraw(23.5))
    }

    @Test
    fun `withdraw amount greater than balance throws IllegalArgumentException`() {
        val ba = BankAccount(23.5)
        assertThrows<IllegalArgumentException> { ba.withdraw(24.5) }
    }

    @Test
    fun `withdraw negative amount throws IllegalArgumentException`() {
        val ba = BankAccount(23.5)
        assertThrows<IllegalArgumentException> { ba.withdraw(-0.1) }
    }

    @Test
    fun `withdraw zero amount throws IllegalArgumentException`() {
        val ba = BankAccount(23.5)
        assertThrows<IllegalArgumentException> { ba.withdraw(-0.0) }
    }

    @Test
    fun `getBalance returns the correct balance`() {
        val ba = BankAccount(23.5)
        assertEquals(23.5, ba.getBalance())
    }
}
