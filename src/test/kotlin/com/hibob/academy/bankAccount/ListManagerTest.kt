package com.hibob.academy.bankAccount

import com.hibob.bankAccount.ListManager
import com.hibob.bankAccount.PeopleStatistics
import com.hibob.bankAccount.Person
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ListManagerTest {

    // addPerson tests:

    @Test
    fun `adding a unique person`() {
        val lm = ListManager()
        val per = Person("Israel", 29)
        lm.addPerson(Person("Israel", 29))

        assertEquals(per, lm.get()[0])
    }

    @Test
    fun `adding a duplicate person and ensure it throws the expected exception`() {
        val lm = ListManager()
        val per = Person("Israel", 29)
        lm.addPerson(per)
        org.junit.jupiter.api.assertThrows<IllegalArgumentException>
        {  lm.addPerson(per) }

    }

    @Test
    fun `adding multiple people, checking that the list grows appropriately`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 28)
        assertEquals(lm.get().size, 0)
        lm.addPerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        assertEquals(lm.get().size, 3)
    }

    // removePerson fun test:

    @Test
    fun `removing a person that exists in the list`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)
        assertEquals(lm.get().size, 0)
        lm.addPerson(per1)
        assertEquals(lm.get().size, 1)
        lm.removePerson(per1)
        assertEquals(lm.get().size, 0)
    }

    @Test
    fun `trying to remove a person that does not exist, ensuring it returns false`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)

        // validation of either name or age is changed from the person inserted
        assertEquals(false,lm.removePerson(Person("Israel", 24)))
        assertEquals(false,lm.removePerson(Person("IsraelC", 29)))

        // validate result with or without people inside the list
        lm.addPerson(per1)
        assertEquals(false,lm.removePerson(Person("Israel", 24)))
        assertEquals(false,lm.removePerson(Person("IsraelC", 29)))
    }

    @Test
    fun `the state of the list after multiple add and remove operations`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 28)
        lm.addPerson(per1)
        lm.removePerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        lm.addPerson(per1)
        lm.removePerson(per3)

        assertEquals(lm.get(), listOf(per2, per1))
    }

    // getPeopleSortedByAgeAndName:
    @Test
    fun `with an empty list`() {
        val lm = ListManager()
        assertEquals(lm.get(),lm.getPeopleSortedByAgeAndName())
    }

    @Test
    fun `with one person`() {
        val lm = ListManager()
        val per1 = Person("Dani", 28)
        lm.addPerson(per1)
        assertEquals(lm.get(),lm.getPeopleSortedByAgeAndName())
    }

    @Test
    fun `with multiple people to ensure they are sorted first by age, then by name`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 28)
        val per4 = Person("Dang", 28)
        val per5 = Person("Cani", 28)
        val per6 = Person("Aoulo", 5)

        lm.addPerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        lm.addPerson(per4)
        lm.addPerson(per5)
        lm.addPerson(per6)

        val sorted = lm.getPeopleSortedByAgeAndName()
        assertEquals(sorted[0],per6)
        assertEquals(sorted[5],per1)
    }

    @Test
    fun `with edge cases like people with the same name but different ages and vice versa`() {
        val lm = ListManager()
        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 28)
        val per4 = Person("Dang", 28)
        val per5 = Person("Cani", 28)
        val per6 = Person("Aoulo", 5)

        lm.addPerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        lm.addPerson(per4)
        lm.addPerson(per5)
        lm.addPerson(per6)

        val sorted = lm.getPeopleSortedByAgeAndName()
        assertEquals(sorted[0],per6)
        assertEquals(sorted[5],per1)
    }

    // calculateStatistics func tests:
    @Test
    fun `null should be returned if list is empty`() {
        val lm = ListManager()
        assertNull(lm.calculateStatistics())
    }

    @Test
    fun `calculated avg age`() {
        val lm = ListManager()

        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 27)
        val per4 = Person("Dang", 25)
        val per5 = Person("Cani", 26)
        val per6 = Person("Aoulo", 27)

        lm.addPerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        lm.addPerson(per4)
        lm.addPerson(per5)
        lm.addPerson(per6)

        val expectedAgeCount = mapOf(
            25 to 1,
            26 to 1,
            27 to 2,
            28 to 1,
            29 to 1
        )

        assertEquals(PeopleStatistics(27.0, per4, per1, expectedAgeCount), lm.calculateStatistics())
    }

    /// !!!!
    // -> the func return the youngest not sorted by name, from the least, even if it has equal
    /// !!!!
    /*
    @Test
    fun `no youngest`() {
        val lm = ListManager()

        val per1 = Person("Israel", 29)
        val per2 = Person("Israel", 28)
        val per3 = Person("Dani", 27)
        val per4 = Person("Dang", 26)
        val per5 = Person("Cani", 26)
        val per6 = Person("Aoulo", 27)

        lm.addPerson(per1)
        lm.addPerson(per2)
        lm.addPerson(per3)
        lm.addPerson(per4)
        lm.addPerson(per5)
        lm.addPerson(per6)

        val expectedAgeCount = mapOf(
            25 to 1,
            26 to 1,
            27 to 2,
            28 to 1,
            29 to 1
        )

        assertNull(lm.calculateStatistics())
    } */
}