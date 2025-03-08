Live Coding Project Pairing Exercise
========

# TODO's Within the App
----------
## EmployeeResponse
* TODO: create model/s needed for JSON coming from employees.json

## EmployeeService
* Senior TODO: Make getEmployees() reactive
  * exp: use rx, coroutines or rx java

## YumEmployeeServices
* TODO: replace the StubEmployeeResponse class with your Employee model
* TODO: update what getEmployees() returns
* TODO: add dependency injection for moshi instance
* Senior TODO: Improve getEmployees implementation. Expectations:
    * Inject moshi as a dependency
    * Implement reactivity for getEmployees
    * Extract reader to only load the json file once
    * Load the json file on a background thread
    * Add the appropriate model for Employee
* Bonus point for Senior: Implement adding an employee. exp:
  * allowed to add a random employee without UI input fields
  * 

## EmployeesAdapter
* TODO: replace the StubEmployee class with your Employee model
* TODO: implement the areItemsTheSame method based on your model -- remove for senior 
* TODO: implement the bind method to show the following: <format>
* Senior TODO: Filter the list (by employee location)
  * Let them think of this from the ground up. Only give them hints if they struggle

## MainActivity
* Senior TODO: set up and populate the employees recycler
* Senior TODO: filter the employee list by the selected location
* Senior TODO: Provide the employee viewModel and service via DI (manual or otherwise)
  * if manual, they'll probably use application class
  * if framework, we should have Dagger or Hilt full loaded in the project

## Additional task for Senior
* Persist filter selection. Expectations
  * allowed to use any medium eg shared prefs etc
  * a ui indicator on filter list is NOT required

## Additional task for Senior 
* Senior TODO: Navigate to an employee detail screen and populate with the selected item. Expectations
  * should we have a prebuilt detail screen? so they mainly focus on navigating, item click & wiring the viewmodel?
  * should we ask them to use fragments?
  * 
* 


## activity_main
TODO: add text above the RecyclerView showing total employees
* 
## item_employee
* TODO: refactor UI

## Extra:
* implement a unit test


## Senior Action items:
* remove todos (if giving candidate zip beforehand)
* implement model
* if doing employee detail screen, fragments would need to be added
* add dagger (optional)


Interview Structure
## First 15 mins:
* Intro & explaining interview structure
  * candidate should be reminded to vocalize their thoughtprocess
  * 
* Project setup & first run
* Project walkthrough

## ~50 minutes
* Interview tasks

## Final 10 minutes
* What would you do differently, given more time
* Questions