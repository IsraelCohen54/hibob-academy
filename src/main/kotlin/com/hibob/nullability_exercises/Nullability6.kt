data class Department(val name: String?, val manager: EmployeeDetails?)
data class EmployeeDetails(val name: String?, val contactInfo: ContactInfo?)
data class ContactInfo(val email: String?, val phone: String?)

fun main() {
    // Task: Print each department's name and manager's contact information.
    // If any information is missing, use appropriate defaults.

    val departments = listOf(
        Department("Engineering", EmployeeDetails("Alice", ContactInfo("alice@example.com", null))),
        Department("Human Resources", null),
        Department(null, EmployeeDetails("Bob", ContactInfo(null, "123-456-7890"))),
        Department("Marketing", EmployeeDetails(null, ContactInfo("marketing@example.com", "987-654-3210")))
    )

    departments.forEach { department ->
        val departmentName = department.name ?: "no department name received"
        val managerInfo = department.manager?.let { manager ->
            val employeeName = manager.name ?: "no EmployeeDetails name received"
            val contactInfo = manager.contactInfo?.let { contact ->
                val email = contact.email ?: "no manager email received"
                val phone = contact.phone ?: "no manager phone received"
                "$email, $phone"
            } ?: "no manager contactInfo received"
            "$employeeName, $contactInfo"
        } ?: "no manager received"

        println("$departmentName: $managerInfo")
    }
}