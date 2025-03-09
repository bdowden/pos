package dev.yum.pairingexercisecompleted

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.yum.pairingexercise.R
import dev.yum.pairingexercise.databinding.ActivityMainBinding
import dev.yum.pairingexercisecompleted.models.Employee
import dev.yum.pairingexercisecompleted.models.Employee.Location
import dev.yum.pairingexercisecompleted.services.YumEmployeeService
import dev.yum.pairingexercisecompleted.viewmodels.EmployeeViewModel
import dev.yum.pairingexercisecompleted.views.EmployeesAdapter
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val employeesAdapter = EmployeesAdapter()

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.employeesRecycler.adapter = employeesAdapter
        binding.employeesRecycler.layoutManager = LinearLayoutManager(this)

        binding.addButton.setOnClickListener {
            showAddDialog { newEmployee ->
                // handle adding an employee here
                Toast.makeText(this, newEmployee.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        lifecycleScope.launch {
            employeeViewModel.getEmployees()
                .collect { employeeList ->
                    binding.numberEmployees.text = getString(R.string.total_num_employees, employeeList.size)
                    employeesAdapter.setData(employeeList)
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {/*
            R.id.sort_austin -> employeeViewModel.filterList(Location.AUSTIN)
            R.id.sort_chicago -> employeeViewModel.filterList(Location.CHICAGO)
            R.id.sort_new_york -> employeeViewModel.filterList(Location.NEW_YORK)
            R.id.sort_all -> employeeViewModel.filterList(Location.ALL)*/
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showAddDialog(onEmployeeAdd: (Employee) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_employee)

        @SuppressLint("InflateParams")
        val view = LayoutInflater.from(this).inflate(R.layout.add_employee_dialog, null)
        val employeeNameInput = view.findViewById<TextInputEditText>(R.id.employee_name_input)
        employeeNameInput.addTextChangedListener {
            employeeNameInput.error = "Employee name required".takeIf { employeeNameInput.text.isNullOrBlank() }
        }
        val employeeTitleInput = view.findViewById<TextInputEditText>(R.id.employee_title_input)
        employeeTitleInput.addTextChangedListener {
            employeeTitleInput.error = "Employee title required".takeIf { employeeTitleInput.text.isNullOrBlank() }
        }
        val locationsContainer = view.findViewById<LinearLayout>(R.id.locations_container)
        val locationsErrorIcon = view.findViewById<View>(R.id.locations_error_icon)

        val selectedLocations = mutableSetOf<Location>()
        Location.entries.filter { it != Location.ALL }.forEach { location ->
            val checkbox = CheckBox(this)
            checkbox.text = location.displayName
            checkbox.setOnCheckedChangeListener { _, _ ->
                if (!selectedLocations.remove(location)) {
                    selectedLocations.add(location)
                }
                locationsErrorIcon.visibility = View.VISIBLE.takeIf { selectedLocations.isEmpty() } ?: View.GONE
            }
            locationsContainer.addView(checkbox)
        }

        builder.setView(view)
        builder.setPositiveButton(R.string.add, null)

        val dialog = builder.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            employeeNameInput.error = "Employee name required".takeIf { employeeNameInput.text.isNullOrBlank() }
            employeeTitleInput.error = "Employee title required".takeIf { employeeTitleInput.text.isNullOrBlank() }
            locationsErrorIcon.visibility = View.VISIBLE.takeIf { selectedLocations.isEmpty() } ?: View.GONE
            if (employeeNameInput.error == null && employeeTitleInput.error == null && locationsErrorIcon.visibility == View.GONE) {
                dialog.dismiss()
                val newEmployee = Employee(
                    id = UUID.randomUUID().toString(),
                    name = employeeNameInput.text.toString(),
                    title = employeeTitleInput.text.toString(),
                    locations = selectedLocations.toList()
                )
                onEmployeeAdd(newEmployee)
            }
        }
    }
}
