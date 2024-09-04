package com.hibob.ex5_classes_exercise

data class Location(val street: String, val city: String, val county: String, val zipCode: Int?=null, val postCode: Int?=null)
{
    init {
        // Ensure that at least one of zipCode or postCode is provided
        require(zipCode != null || postCode != null) {
            "Either zipCode or postCode must be provided, but not both."
        }
    }
}

open class Meeting(val name: String, val locationDetails: List<Location>) {
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

    // can add check if us or uk are within the list to make the diff of zipcode etc...
}

class PersonalReview(name: String, locationDetails: List<Location>, participant: Participant, review: List<String>)
    : Meeting(name, locationDetails) {
    init {
        println("success creation of the class")
    }
}
