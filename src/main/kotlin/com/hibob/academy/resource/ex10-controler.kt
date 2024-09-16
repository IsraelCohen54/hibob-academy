package com.hibob.academy.resource
import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import com.hibob.academy.service.PetService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import org.springframework.beans.factory.annotation.Autowired


@Path("/api")
class DaoController {

    @Autowired
    private lateinit var petDao: PetDao
    private val petService:PetService = PetService(petDao)

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


    @POST
    @Path("/{companyId}/{petId}/adopt/{ownerId}")
    @Produces("application/json")
    fun adoptPet(@PathParam("petId") petId: Long, @PathParam("ownerId") ownerId: Long, @PathParam("companyId") companyId: Long): Response {
        petService.adoptPet(petId, ownerId, companyId)
        return Response.ok("Pet $petId adopted by owner $ownerId").build()
    }

    @GET
    @Path("/{petId}/owner")
    @Produces("application/json")
    fun getOwnerByPetId(@PathParam("petId") petId: Long): Response {
        val owner = petService.getOwnerByPetId(petId)
           return Response.ok(owner).build()
    }
}
