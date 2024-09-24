package com.feedback.dao


import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component

@Component
class EmployeeDao(private val sql: DSLContext) {

    private val employeeTable = EmployeeTable.instance

    private val employeeTableMapper = RecordMapper<Record, Employee> { record ->
        Employee(
            id = record[employeeTable.id],
            companyId = record[employeeTable.companyId],
            firstName = record[employeeTable.firstName],
            lastName = record[employeeTable.lastName],
            role = RoleType.fromString(record[(employeeTable.role)]),
            department = DepartmentType.fromString(record[(employeeTable.department)])
        )
    }

    fun getEmployee(companyId: Long, employeeId: Long): Employee? {
        return sql.selectFrom(employeeTable)
            .where(employeeTable.id.eq(employeeId))
            .fetchOne(employeeTableMapper)
    }

    fun insertEmployee(employee: Employee): Long {
        return sql.insertInto(employeeTable)
            .set(employeeTable.firstName, employee.firstName)
            .set(employeeTable.lastName, employee.lastName)
            .set(employeeTable.role, employee.role.toString())
            .set(employeeTable.department, employee.department.toString())
            .set(employeeTable.companyId, employee.companyId)
            .returning(employeeTable.id)
            .fetchOne()
            ?.get(employeeTable.id)
            ?: throw IllegalStateException("Failed to retrieve the generated employee ID.")
    }
}
