package dev.yum.pairingexercisecompleted.services

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.yum.pairingexercisecompleted.models.Employee
import dev.yum.pairingexercisecompleted.models.EmployeeResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class YumEmployeeService(private val context: Context, private val moshi: Moshi) : EmployeeService {

    override suspend fun getEmployees(): Flow<List<Employee>> {
        //delay(5000)

        val reader = context.assets.open("employees.json").bufferedReader().use { it.readText() }
        val jsonAdapter: JsonAdapter<EmployeeResponse> = moshi.adapter(EmployeeResponse::class.java)
        val employees = jsonAdapter.fromJson(reader)?.employees ?: return emptyFlow()
        return flowOf(employees)
    }
}
