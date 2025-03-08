package dev.yum.pairingexercisecompleted

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.yum.pairingexercise.R
import dev.yum.pairingexercisecompleted.models.Employee
import dev.yum.pairingexercisecompleted.services.YumEmployeeService
import dev.yum.pairingexercisecompleted.viewmodels.EmployeeViewModel
import dev.yum.pairingexercisecompleted.viewmodels.Status
import java.util.UUID

class ComposeActivity : ComponentActivity() {

    private lateinit var employeeViewModel: EmployeeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moshiInstance = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val service = YumEmployeeService(this, moshiInstance)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return EmployeeViewModel(service) as T
            }
        }
        employeeViewModel = ViewModelProvider(this, factory)[EmployeeViewModel::class.java]

        setContent {
            val viewState by employeeViewModel.employeeState.collectAsState()

            var showAddDialog by remember { mutableStateOf(false) }
            Scaffold(
                topBar = {
                    TopBar(
                        currentlySelected = viewState.selectedFilter,
                        onLocationClicked = employeeViewModel::filterList
                    )
                },
                floatingActionButton = {
                    Fab { showAddDialog = true }
                }
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    if (viewState.status is Status.Loading) {
                        Text(
                            text = "Loading...",
                            modifier = Modifier.padding(15.dp)
                        )

                    } else if (viewState.status is Status.Completed) {

                        if (viewState.employeeList.isNotEmpty()) {
                            LazyColumn {
                                items(viewState.employeeList) {
                                    EmployeeListItem(employee = it)
                                }
                            }
                        } else {
                            Text(
                                text = "No results found",
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }

                if (showAddDialog) {
                    AddEmployeeDialog(
                        onDismiss = { showAddDialog = false },
                        onConfirmClicked = employeeViewModel::saveNewEmployee
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(currentlySelected: Employee.Location, onLocationClicked: (Employee.Location) -> Unit) {
        var showDropDownMenu by remember { mutableStateOf(false) }
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(onClick = { showDropDownMenu = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.List,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showDropDownMenu,
                    onDismissRequest = { showDropDownMenu = false }
                ) {
                    Employee.Location.entries.forEach { location ->
                        val color = if (currentlySelected == location) {
                            Color.LightGray
                        } else {
                            Color.Unspecified
                        }
                        DropdownMenuItem(
                            modifier = Modifier.background(color),
                            text = { Text(text = location.displayName) },
                            onClick = {
                                onLocationClicked(location)
                                showDropDownMenu = false
                            }
                        )
                    }
                }
            }
        )
    }

    @Composable
    fun Fab(onFabClick: () -> Unit) {
        FloatingActionButton(
            onClick = onFabClick
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = ""
            )
        }
    }

    @Composable
    fun EmployeeListItem(employee: Employee) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.employee_item_padding))) {
            Text(
                text = employee.name,
                fontSize = dimensionResource(id = R.dimen.employee_name_text_size).value.sp,
            )
            Column(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.employee_info_padding_start))
            ) {
                Text(
                    text = stringResource(R.string.employee_title, employee.title),
                    fontSize = dimensionResource(id = R.dimen.employee_info_text_size).value.sp,
                )
                Text(
                    text = stringResource(R.string.employee_locations, employee.locations.joinToString { it.displayName }),
                    fontSize = dimensionResource(id = R.dimen.employee_info_text_size).value.sp,

                    )
            }
        }
    }

    @Composable
    private fun AddEmployeeDialog(
        onDismiss: () -> Unit,
        onConfirmClicked: (Employee) -> Unit,
    ) {

        var employeeNameText by rememberSaveable { mutableStateOf("") }
        var employeeNameValid by remember { mutableStateOf(true) }

        var employeeTitleText by rememberSaveable { mutableStateOf("") }
        var employeeTitleValid by remember { mutableStateOf(true) }

        var selectedLocations by rememberSaveable { mutableStateOf(emptySet<Employee.Location>()) }
        var selectedLocationsValid by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(id = R.string.add_employee)) },
            text = {
                Column {
                    TextField(
                        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.add_field_vertical_spacing)),
                        isError = !employeeNameValid,
                        value = employeeNameText,
                        onValueChange = {
                            employeeNameText = it
                            employeeNameValid = employeeNameText.isNotBlank()
                        },
                        trailingIcon = {
                            if (!employeeNameValid) {
                                ErrorIcon()
                            }
                        },
                        label = { Text(text = stringResource(id = R.string.employee_name_label)) }
                    )

                    TextField(
                        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.add_field_vertical_spacing)),
                        isError = !employeeTitleValid,
                        value = employeeTitleText,
                        onValueChange = {
                            employeeTitleText = it
                            employeeTitleValid = employeeTitleText.isNotBlank()
                        },
                        trailingIcon = {
                            if (!employeeTitleValid) {
                                ErrorIcon()
                            }
                        },
                        label = { Text(text = stringResource(id = R.string.employee_title_label)) }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.employee_locations_label))
                        if (!selectedLocationsValid) {
                            ErrorIcon()
                        }
                    }

                    Employee.Location.entries.filter { it != Employee.Location.ALL }.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = selectedLocations.contains(it), onCheckedChange = { _ ->
                                val locations = selectedLocations.toMutableSet()
                                if (!locations.remove(it)) {
                                    locations.add(it)
                                }
                                selectedLocations = locations.toSet()
                                selectedLocationsValid = selectedLocations.isNotEmpty()
                            })
                            Text(text = it.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        employeeNameValid = employeeNameText.isNotBlank()
                        employeeTitleValid = employeeTitleText.isNotBlank()
                        selectedLocationsValid = selectedLocations.isNotEmpty()
                        if (employeeNameValid && employeeTitleValid && selectedLocationsValid) {
                            val newEmployee = Employee(
                                id = UUID.randomUUID().toString(),
                                name = employeeNameText,
                                locations = selectedLocations.toList(),
                                title = employeeTitleText
                            )
                            onConfirmClicked(newEmployee)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.add),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
        )
    }

    @Composable
    private fun ErrorIcon() {
        Icon(
            tint = MaterialTheme.colorScheme.error,
            imageVector = Icons.Outlined.Warning,
            contentDescription = ""
        )
    }

}
