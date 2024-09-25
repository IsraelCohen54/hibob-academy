package com.feedback.dao


import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class EmployeeDao(private val sql: DSLContext) {

    private val employeeTable = EmployeeTable.instance
    private val employeeTableMapper = RecordMapper<Record, RetrieveEmployeeRequest> { record ->
        RetrieveEmployeeRequest(
            id = record[employeeTable.id],
            firstName = record[employeeTable.firstName],
            lastName = record[employeeTable.lastName],
            role = RoleType.fromString(record[(employeeTable.role)]),
            department = DepartmentType.fromString(record[(employeeTable.department)])
        )
    }

    fun getEmployee(userDetails: LoggedInUser): RetrieveEmployeeRequest? {
        return sql.selectFrom(employeeTable)
            .where(
                employeeTable.id.eq(userDetails.employeeId)
                    .and(employeeTable.companyId.eq(userDetails.companyId))
            )
            .fetchOne(employeeTableMapper)
    }

    fun insertEmployee(userDetails: LoggedInUser, employee: EmployeeCreationRequest): Long {
        return sql.insertInto(employeeTable)
            .set(employeeTable.firstName, employee.firstName)
            .set(employeeTable.lastName, employee.lastName)
            .set(employeeTable.role, employee.role.toString())
            .set(employeeTable.department, employee.department.toString())
            .set(employeeTable.companyId, userDetails.companyId)
            .returning(employeeTable.id)
            .fetchOne()
            ?.get(employeeTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated employee ID.")
    }
}
