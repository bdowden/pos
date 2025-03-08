package dev.yum.pairingexercisecompleted.viewmodels

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.yum.pairingexercisecompleted.models.Employee
import dev.yum.pairingexercisecompleted.services.EmployeeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployeeListViewState(
    val employeeList: List<Employee> = emptyList(),
    val selectedFilter: Employee.Location = Employee.Location.ALL,
    val status: Status = Status.NotStarted(),
)

sealed class Status() {
    class NotStarted : Status()
    class Loading : Status()
    class Completed : Status()
}

class EmployeeViewModel(private val employeeService: EmployeeService) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeListViewState())
    val employeeState = _state.asStateFlow()

    private val allEmployees = mutableListOf<Employee>()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy (
                    status = Status.Loading()
                )
            }

            employeeService.getEmployees().collect { employees ->
               allEmployees.clear()
                allEmployees.addAll(employees)

                _state.update {
                    it.copy(
                        employeeList = allEmployees,
                        status = Status.Completed(),
                    )
                }
            }
        }
    }

    suspend fun getEmployees() = employeeService.getEmployees()

    fun filterList(location: Employee.Location) {
        val filteredEmployees = if (location == Employee.Location.ALL) {
            allEmployees
        } else {
            allEmployees.filter {
                it.locations.contains(location)
            }
        }

        _state.update {
            it.copy(
                employeeList = filteredEmployees,
                selectedFilter = location,
            )
        }
    }

    fun saveNewEmployee(newEmployee: Employee) {
        allEmployees.add(newEmployee)

        val currentFilter = _state.value.selectedFilter

        filterList(currentFilter)
    }
}
