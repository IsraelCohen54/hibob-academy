package com.hibob.academy.resource

import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import java.time.LocalDate

@Path("/pets")
class PetController {

    // GET request to retrieve pet type by ID
    @GET
    @Path("/{petId}/{type}")
    @Produces("application/json")
    fun getPetType(@PathParam("petId") petId: Long): Response {
        return if (petId < 0) {
            Response.status(Status.NOT_FOUND).entity("No pet found").build()
        } else {
            Response.ok("parrot").build()
        }
    }

    // POST request to create a new pet
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    fun createPet(pet: Pet): Response {
        // Simulate creation logic
        return Response.status(Status.CREATED).entity("Pet ${pet.name} created successfully").build()
    }

    // PUT request to update an existing pet
    @PUT
    @Path("/{petId}")
    @Consumes("application/json")
    @Produces("application/json")
    fun updatePet(@PathParam("petId") petId: Long, pet: Pet): Response {
        return if (petId < 0) {
            Response.status(Status.NOT_FOUND).entity("No pet found to update").build()
        } else {
            Response.ok("Pet ${petId} updated successfully").build()
        }
    }

    // DELETE request to delete a pet by ID
    @DELETE
    @Path("/{petId}")
    @Produces("application/json")
    fun deletePet(@PathParam("petId") petId: Long): Response {
        return if (petId < 0) {
            throw (NotFoundException("No pet with $petId found to delete"))
        } else {
            Response.ok("Pet $petId deleted successfully").build()
        }
    }

    // Example endpoint to throw an exception
    @GET
    @Path("/error")
    @Produces("application/json")
    fun throwError(): Response {
        throw RuntimeException("An error occurred")
    }
}

data class Pet(
    val id: Long,
    val name: String,
    val type: String,
    val companyId: Int,
    val dateOfArrival: LocalDate
)
