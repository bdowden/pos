package dev.yum.pairingexercisecompleted.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.yum.pairingexercise.R
import dev.yum.pairingexercise.databinding.ItemEmployeeBinding
import dev.yum.pairingexercisecompleted.models.Employee

class EmployeesAdapter : ListAdapter<Employee, EmployeesAdapter.EmployeeViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder =
        EmployeeViewHolder(
            ItemEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int): Unit =
        holder.bind(getItem(position), holder.itemView.context)

    fun setData(employeeList: List<Employee>) {
        submitList(employeeList)
    }

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: Employee, context: Context) {
            with(binding) {
                employeeName.text = employee.name
                employeeTitle.text = context.getString(R.string.employee_title, employee.title)
                val locations = employee.locations.joinToString { it.displayName }
                employeeLocations.text = context.getString(R.string.employee_locations, locations)
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Employee>() {
            override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean =
                oldItem == newItem
        }
    }
}
