package com.hibob.nullability_exercises

/**
 * Instructions:
 *
 * Traverse through the company structure starting from departments to teams and finally to team members.
 * For each level (company, department, team, leader, members), check for null values and print appropriate information.
 * Ensure that every piece of information printed includes a fallback for null values using the Elvis operator and that
 * blocks of code dependent on non-null values are executed using ?.let.
 *
 */
data class Company(val name: String?, val departments: List<DepartmentDetails?>?)
data class DepartmentDetails(val name: String?, val teams: List<Team?>?)
data class Team(val name: String?, val leader: Leader?, val members: List<Member?>?)
data class Leader(val name: String?, val title: String?)
data class Member(val name: String?, val role: String?)

const val READABILITY = 1

fun initializeCompany(): Company {
    return Company(
        "Tech Innovations Inc.",
        listOf(
            DepartmentDetails("Engineering", listOf(
                Team("Development", Leader("Alice Johnson", "Senior Engineer"), listOf(Member("Bob Smith", "Developer"), null)),
                Team("QA", Leader(null, "Head of QA"), listOf(Member(null, "QA Analyst"), Member("Eve Davis", null))),
                null
            )),
            DepartmentDetails(null, listOf(
                Team("Operations", null, listOf(Member("John Doe", "Operator"), Member("Jane Roe", "Supervisor")))
            )),
            null
        )
    )
}

fun main11() {
    val company = initializeCompany()

    // Print company name with a fallback if it's null
    println("Company Name: ${company.name ?: "Name Unknown"}")

    // Traverse departments
    company.departments?.forEachIndexed { deptIndex, department ->
        department?.let {
            println("  Department ${deptIndex + READABILITY} Name: ${it.name ?: "Name Unknown"}")

            // Traverse teams
            it.teams?.forEachIndexed { teamIndex, team ->
                team?.let { teamIt ->
                    println("    Team ${teamIndex + READABILITY} Name: ${teamIt.name ?: "Name Unknown"}")
                    println("    Team Leader: ${teamIt.leader?.name ?: "Leader Name Unknown"}, ${teamIt.leader?.title ?: "Leader Title Unknown"}")

                    // Traverse members
                    teamIt.members?.forEachIndexed { memberIndex, member ->
                        member?.let {
                            println("      Member ${memberIndex + READABILITY}: ${it.name ?: "Member Name Unknown"}, ${it.role ?: "Member Role Unknown"}")
                        } ?: run {
                            println("      Member ${memberIndex + READABILITY}: Member data is not available.")
                        }
                    }
                } ?: run {
                    println("    Team ${teamIndex + READABILITY}: Team data is not available.")
                }
            }
        } ?: run {
            println("  Department ${deptIndex + READABILITY}: Department data is not available.")
        }
    }
}
