package com.hibob.academy.resource

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/pets")
class PetController {

    // GET request to retrieve pet type by ID
    @GetMapping("/{petId}/type")
    fun getPetType(@PathVariable petId: Long): ResponseEntity<String> {
        return if (petId < 0) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pet found")
        } else {
            ResponseEntity.ok("parrot")
        }
    }

    // POST request to create a new pet
    @PostMapping
    fun createPet(@RequestBody pet: Pet): ResponseEntity<String> {
        // Simulate creation logic
        return ResponseEntity.status(HttpStatus.CREATED).body("Pet ${pet.name} created successfully")
    }

    // PUT request to update an existing pet
    @PutMapping("/{petId}")
    fun updatePet(@PathVariable petId: Long, @RequestBody pet: Pet): ResponseEntity<String> {
        return if (petId < 0) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pet found to update")
        } else {
            ResponseEntity.ok("Pet ${petId} updated successfully")
        }
    }

    // DELETE request to delete a pet by ID
    @DeleteMapping("/{petId}")
    fun deletePet(@PathVariable petId: Long): ResponseEntity<String> {
        return if (petId < 0) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pet found to delete")
        } else {
            ResponseEntity.ok("Pet ${petId} deleted successfully")
        }
    }

    // Example endpoint to throw an exception
    @GetMapping("/error")
    fun throwError(): ResponseEntity<String> {
        throw RuntimeException("An error occurred")
    }
}

// Data class for Pet (for simplicity)
data class Pet(
    val id: Long,
    val name: String,
    val type: String,
    val companyId: Int,
    val dateOfArrival: LocalDate
)
