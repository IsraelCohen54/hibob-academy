package com.feedback.dao


import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class EmployeeDao(private val sql: DSLContext) {

    private val employeeTable = EmployeeTable.instance

    private val employeeTableMapper = RecordMapper<Record, PersistedEmployee> { record ->
        PersistedEmployee(
            id = record[employeeTable.id],
            firstName = record[employeeTable.firstName],
            lastName = record[employeeTable.lastName],
            role = RoleType.fromString(record[(employeeTable.role)]),
            department = DepartmentType.fromString(record[(employeeTable.department)])
        )
    }

    fun getEmployee(userDetails: LoggedInUser): PersistedEmployee? {
        return sql.selectFrom(employeeTable)
            .where(employeeTable.id.eq(userDetails.employeeId)
                .and(employeeTable.companyId.eq(userDetails.companyId)))
            .fetchOne(employeeTableMapper)
    }

    fun insertEmployee(userDetails: LoggedInUser, employeeCreationRequest: EmployeeCreationRequest): Long {
        return sql.insertInto(employeeTable)
            .set(employeeTable.firstName, employeeCreationRequest.firstName)
            .set(employeeTable.lastName, employeeCreationRequest.lastName)
            .set(employeeTable.role, employeeCreationRequest.role.name)
            .set(employeeTable.department, employeeCreationRequest.department.name)
            .set(employeeTable.companyId, userDetails.companyId)
            .returning(employeeTable.id)
            .fetchOne()
            ?.get(employeeTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated employee ID.")
    }
}
