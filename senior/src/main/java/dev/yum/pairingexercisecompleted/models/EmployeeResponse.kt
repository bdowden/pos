package dev.yum.pairingexercisecompleted.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmployeeResponse(val employees: List<Employee>)

@JsonClass(generateAdapter = true)
data class Employee(
    val id: String,
    val name: String,
    val locations: List<Location>,
    val title: String
) {
    enum class Location(val displayName: String) {
        @Json(name = "Chicago")
        CHICAGO("Chicago"),

        @Json(name = "New York")
        NEW_YORK("New York"),

        @Json(name = "Austin")
        AUSTIN("Austin"),
        ALL("All")
    }
}



