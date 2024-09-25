package com.feedback.service

import com.feedback.dao.EmployeeDao
import com.feedback.dao.LoggedInUser
import com.feedback.dao.PersistedEmployee
import org.springframework.stereotype.Service

@Service
class EmployeeFetcher(private val employeeDao: EmployeeDao) {

    fun getEmployeeDetails(userDetails: LoggedInUser): PersistedEmployee {
        return employeeDao.getEmployee(userDetails)
            ?: throw IllegalStateException("No employee found with ID ${userDetails.employeeId}")
    }
}
