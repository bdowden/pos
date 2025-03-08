package dev.yum.pairingexercisecompleted.services

import dev.yum.pairingexercisecompleted.models.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeService {
    /**
     * Emits an a list of [Employee]s
     */
    suspend fun getEmployees(): Flow<List<Employee>>
}
