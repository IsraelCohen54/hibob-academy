package com.hibob.ex5_classes_exercise

interface Location {
    val street: String
    val city: String
    val county: String
}

data class Us(val zipCode: Int, override val street: String, override val city: String, override val county: String) : Location

data class Uk(val postalCode: Int, override val street: String, override val city: String, override val county: String) : Location

data class LocationDetails(val loc:Location) {}

open class Meeting(private val name: String, private val locationDetails: List<LocationDetails>) {
    private lateinit var participant: MutableList<Participant>

    fun addParticipant(participant: Participant) {
        this.participant.add(participant)
    }

    fun removeParticipant(participant: Participant) {
        this.participant.remove(participant)
    }

    fun printMeeting() {
        println(this.name + " " + this.locationDetails.listIterator())
    }
}

class PersonalReview(name: String, locationDetails: List<LocationDetails>, participant: Participant, review: List<String>)
    : Meeting(name, locationDetails) {
    init {
        println("success creation of the class")
    }
}
