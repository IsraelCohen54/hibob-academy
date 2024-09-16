package com.hibob.academy.resource
import com.hibob.academy.dao.Pet
import com.hibob.academy.service.PetService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Path("/api")
@Produces("application/json")
@Consumes("application/json")
class DaoController @Inject constructor(private val petService: PetService) {


    // GET request to retrieve pet type by ID
    @GET
    @Path("/{petId}/{type}")
    fun getPetType(@PathParam("petId") petId: Long): Response {
        return if (petId < 0) {
            Response.status(Status.NOT_FOUND).entity("No pet found").build()
        } else {
            Response.ok("parrot").build()
        }
    }

    // POST request to create a new pet
    @POST
    fun createPet(pet: Pet): Response {
        // Simulate creation logic
        return Response.status(Status.CREATED).entity("Pet ${pet.name} created successfully").build()
    }

    // PUT request to update an existing pet
    @PUT
    @Path("/{petId}")
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
    fun throwError(): Response {
        throw RuntimeException("An error occurred")
    }


    @POST
    @Path("/{companyId}/{petId}/adopt/{ownerId}")
    fun adoptPet(@PathParam("petId") petId: Long, @PathParam("ownerId") ownerId: Long, @PathParam("companyId") companyId: Long): Response {
        petService.adoptPet(petId, ownerId, companyId)
        return Response.ok("Pet $petId adopted by owner $ownerId").build()
    }

    @GET
    @Path("/{companyId}/{petId}/owner")
    fun getOwnerByPetId(@PathParam("petId") petId: Long, @PathParam("companyId") companyId: Long): Response {
        val owner = petService.getOwnerByPetId(petId, companyId)
           return Response.ok(owner).build()
    }

    @GET
    @Path("/{ownerId}")
    @Consumes("application/json")
    @Produces("application/json")
    fun getOwnerPets(@PathParam("ownerId") ownerId: Long): Response {
        val ownerPets = petService.getOwnerPets(ownerId)
        return Response.ok(ownerPets).build()
    }

    @GetMapping("/countByType")
    fun countPetsByType(@RequestParam companyId: Long): ResponseEntity<Map<PetType, Int>> {
        return ResponseEntity.ok(petService.countPetsByType(companyId))
    }
}
