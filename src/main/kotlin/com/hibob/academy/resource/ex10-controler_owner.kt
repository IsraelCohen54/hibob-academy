package com.hibob.academy.resource

import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status

@Path("/owners")
class OwnerController {

    // GET request to retrieve owner by ID
    @GET
    @Path("/{ownerId}/name")
    @Produces("application/json")
    fun getOwnerName(@PathParam("ownerId") ownerId: Long): Response {
        // Simulate fetching owner from the database
        val owner = findOwnerById(ownerId)
        return if (owner != null) {
            Response.ok(owner.name).build() // Return owner's name
        } else {
            Response.status(Status.NOT_FOUND).entity("No owner found").build()
        }
    }

    // POST request to create a new owner
    @POST
    @Path("/v1")
    @Consumes("application/json")
    @Produces("application/json")
    fun createOwner1(owner: Owner): Response {
        // Simulate saving owner to the database
        saveOwner(owner)
        return Response.status(Status.CREATED).entity("Owner ${owner.name} created successfully").build()
    }

    // POST request to create a new owner
    @POST
    @Path("/v2")
    @Consumes("application/json")
    @Produces("application/json")
    fun createOwner2(owner: Owner): Response {
        val firstName: String? = owner.name?.split(" ")?.getOrNull(0)
        val lastName: String? = owner.name?.split(" ")?.getOrNull(1)

        // Simulate saving owner to the database
        val newOwner = saveOwner(owner.copy(firstName=firstName, lastName=lastName))
        return Response.status(Status.CREATED).entity("Owner ${newOwner} created successfully").build()
    }

    // PUT request to update an existing owner
    @PUT
    @Path("/{ownerId}")
    @Consumes("application/json")
    @Produces("application/json")
    fun updateOwner(@PathParam("ownerId") ownerId: Long, owner: Owner): Response {
        return if (updateOwnerInDatabase(ownerId, owner)) {
            Response.ok("Owner ${ownerId} updated successfully").build()
        } else {
            Response.status(Status.NOT_FOUND).entity("No owner found to update").build()
        }
    }

    // DELETE request to delete an owner by ID
    @DELETE
    @Path("/{ownerId}")
    @Produces("application/json")
    fun deleteOwner(@PathParam("ownerId") ownerId: Long): Response {
        return if (deleteOwnerFromDatabase(ownerId)) {
            Response.ok("Owner ${ownerId} deleted successfully").build()
        } else {
            Response.status(Status.NOT_FOUND).entity("No owner found to delete").build()
        }
    }

    // Example endpoint to throw an exception
    @GET
    @Path("/error")
    @Produces("application/json")
    fun throwError(): Response {
        throw RuntimeException("An error occurred")
    }

    // Simulated methods for database operations (replace with actual implementations)
    private fun findOwnerById(ownerId: Long): Owner? {
        // Simulate finding owner by ID
        return Owner("John Doe", 1, 123) // Example owner
    }

    private fun saveOwner(owner: Owner) {
        // Simulate saving owner
    }

    private fun updateOwnerInDatabase(ownerId: Long, owner: Owner): Boolean {
        // Simulate updating owner
        return true
    }

    private fun deleteOwnerFromDatabase(ownerId: Long): Boolean {
        // Simulate deleting owner
        return true
    }
}

data class Owner(
    val name: String? = null,
    val companyId: Int,
    val employeeId: Int,
    val firstName : String? = null,
    val lastName : String? = null
)
