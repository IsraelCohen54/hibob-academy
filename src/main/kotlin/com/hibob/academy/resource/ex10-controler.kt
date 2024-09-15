package com.hibob.academy.resource
import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetDao
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import org.springframework.beans.factory.annotation.Autowired


@Path("/api")
class DaoController {

    @Autowired
    private lateinit var petDao: PetDao

    fun setPetDao(petDao: PetDao) {
        this.petDao = petDao
    }

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
        val isPetOwnerLess = petDao.getPetOwnerId(petId)
        isPetOwnerLess?.let {
            try {
                return Response.status(Status.CONFLICT).entity("Pet with ID $petId has already an owner").build()
            } catch (e: Exception) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("got exception: $e").build()
            }
        }?:
        run{
            try {
                petDao.adopt(petId, ownerId, companyId)
                return Response.ok("Pet $petId adopted by owner $ownerId").build()
            }
            catch(e: Exception) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("got exception: $e").build()
            }
        }
    }

    @GET
    @Path("/{petId}/owner")
    @Produces("application/json")
    fun getOwnerByPetId(@PathParam("petId") petId: Long): Response {
        return try {
            val owner = petDao.getPetOwner(petId)
            owner?.let{
                Response.ok(owner).build()
            } ?: Response.status(Status.NOT_FOUND).entity("Owner for pet with ID $petId not found").build()

        } catch (e: Exception) {
            Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred while retrieving owner: $e").build()
        }
    }
}

