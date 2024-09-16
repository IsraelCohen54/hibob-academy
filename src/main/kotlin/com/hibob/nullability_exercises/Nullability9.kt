package com.hibob.nullability_exercises

/**
 * Filter Departments: Identify departments that have either no manager assigned or where the manager's
 * contact information is entirely missing.
 *
 * Email List Compilation: Generate a list of all unique manager emails but exclude any null
 * or empty strings. Ensure the list has no duplicates.
 *
 * Reporting: For each department, generate a detailed report that includes the
 * department name, manager's name, email, and formatted phone number.
 * If any information is missing, provide a placeholder.
 *
 */

data class DepartmentData(val name: String?, val manager: EmployeeData?)
data class EmployeeData(val name: String?, val contactInfo: Contact?)
data class Contact(val email: String?, val phone: String?)

fun main12() {
    val departments = listOf(
        DepartmentData("Engineering", EmployeeData("Alice", Contact("alice@example.com", "123-456-7890"))),
        DepartmentData("Human Resources", null),
        DepartmentData("Operations", EmployeeData("Bob", Contact(null, "234-567-8901"))),
        DepartmentData("Marketing", EmployeeData(null, Contact("marketing@example.com", "345-678-9012"))),
        DepartmentData("Finance", EmployeeData("Carol", Contact("", "456-789-0123")))
    )
    // Implement the features here.

    // 1. Filter Departments
    val departmentsWithIssues = departments.filter { department ->
        department.manager == null || department.manager.contactInfo?.let {
            it.email.isNullOrEmpty() && it.phone.isNullOrEmpty()
        } ?: true
    }
    println("Departments with issues:")
    departmentsWithIssues.forEach { department ->
        println(" - ${department.name ?: "Unnamed com.hibob.nullability_exercises.Department"}")
    }

    // 2. Email List Compilation
    val uniqueEmails = departments.mapNotNull { department ->
        department.manager?.contactInfo?.email?.takeIf { it.isNotEmpty() }
    }.toSet().toList()
    println("\nUnique Manager Emails:")
    uniqueEmails.forEach { email ->
        println(" - $email")
    }

    // 3. Reporting
    println("\ncom.hibob.nullability_exercises.Department Reports:")
    departments.forEach { department ->
        val departmentName = department.name ?: "com.hibob.nullability_exercises.Department Name Unknown"
        val managerName = department.manager?.name ?: "Manager Name Unknown"
        val email = department.manager?.contactInfo?.email?.takeIf { it.isNotEmpty() } ?: "Email Not Available"
        val phone = department.manager?.contactInfo?.phone?.takeIf { it.isNotEmpty() } ?: "Phone Not Available"

        println("com.hibob.nullability_exercises.Department: $departmentName")
        println(" Manager: $managerName")
        println(" Email: $email")
        println(" Phone: $phone")
        println()
    }
}