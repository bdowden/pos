package dev.yum.pairingexercisecompleted.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.yum.pairingexercisecompleted.models.Employee
import dev.yum.pairingexercisecompleted.services.EmployeeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    val _state = MutableStateFlow(EmployeeListViewState())

    val stateFlow = _state.asStateFlow()

    private var allEmployees by mutableStateOf<List<Employee>>(emptyList())

    private val filterUpdateFlow = _state
        .distinctUntilChangedBy { it.selectedFilter }

    private val employeeUpdateFlow = snapshotFlow { allEmployees }

    init {
        viewModelScope.launch {
            _state.update {
                it.copy (
                    status = Status.Loading()
                )
            }

            merge(filterUpdateFlow, employeeUpdateFlow)
                .map {
                    filterEmployees(_state.value.selectedFilter)
                }
                .updateEmployeeList()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
                .launchIn(viewModelScope)

            employeeService.getEmployees().collect { employees ->
                allEmployees = employees

                _state.update {
                    it.copy(
                        status = Status.Completed(),
                    )
                }
            }
        }
    }
    suspend fun getEmployees() = employeeService.getEmployees()

    private fun filterEmployees(filter: Employee.Location) =
        if (filter == Employee.Location.ALL) {
            allEmployees
        } else {
            allEmployees.filter { emp ->
                emp.locations.contains(filter)
            }
        }

    fun setFilterLocation(filter: Employee.Location) {
        _state.update {
            it.copy(
                selectedFilter = filter,
            )
        }
    }

    fun saveNewEmployee(newEmployee: Employee) {
        allEmployees = allEmployees + newEmployee
    }


    private fun Flow<List<Employee>>.updateEmployeeList(): Flow<List<Employee>> =
        onEach { employees ->
            _state.update {
                it.copy(
                    employeeList = employees,
                )
            }
        }
}
