package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.made_project.R
import com.example.made_project.adapters.TaskAdapter
import com.example.made_project.database.DatabaseHelper
import com.example.made_project.models.TaskModel
import com.example.made_project.utils.SharedPrefManager
import com.example.made_project.utils.UiAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class DashboardActivity : BaseActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: View
    private lateinit var searchView: SearchView
    private lateinit var filterSpinner: Spinner
    private var allTasks: List<TaskModel> = emptyList()
    private var activeTasks: List<TaskModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        databaseHelper = DatabaseHelper(this)
        sharedPrefManager = SharedPrefManager(this)

        recyclerView = findViewById(R.id.recyclerRecentTasks)
        emptyStateView = findViewById(R.id.layoutEmptyState)
        searchView = findViewById(R.id.searchTasks)
        filterSpinner = findViewById(R.id.spinnerFilter)

        findViewById<TextView>(R.id.textWelcomeUser).text =
            "${getGreetingMessage()}, ${sharedPrefManager.getUserName()}"
        val addTaskFab = findViewById<FloatingActionButton>(R.id.fabAddTask)
        addTaskFab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
        setupPriorityMatrixClicks()

        taskAdapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
        UiAnimator.animateSequence(
            findViewById(R.id.textWelcomeUser),
            findViewById(R.id.cardProgress),
            findViewById(R.id.layoutMatrixTop),
            findViewById(R.id.layoutMatrixBottom),
            findViewById(R.id.searchTasks),
            findViewById(R.id.spinnerFilter),
            findViewById(R.id.recyclerRecentTasks),
            startDelay = 80L
        )
        UiAnimator.pop(addTaskFab, 260L)

        setupSearch()
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters(searchView.query?.toString().orEmpty(), filterSpinner.selectedItem?.toString().orEmpty())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        setupBottomNavigation(findViewById(R.id.bottomNavigation), R.id.nav_home)
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilters(newText.orEmpty(), filterSpinner.selectedItem?.toString().orEmpty())
                return true
            }
        })
    }

    private fun loadDashboardData() {
        allTasks = databaseHelper.getAllTasks()
        activeTasks = allTasks.filter { it.status != "Completed" }
        val completedCount = allTasks.count { it.status == "Completed" }
        val pendingCount = activeTasks.size

        findViewById<TextView>(R.id.textTotalTasks).text = allTasks.size.toString()
        findViewById<TextView>(R.id.textCompletedTasks).text = completedCount.toString()
        findViewById<TextView>(R.id.textPendingTasks).text = pendingCount.toString()
        findViewById<TextView>(R.id.textIUCount).text = countByPriority("Important and Urgent").toString()
        findViewById<TextView>(R.id.textINCount).text = countByPriority("Important but Not Urgent").toString()
        findViewById<TextView>(R.id.textNUCount).text = countByPriority("Urgent but Not Important").toString()
        findViewById<TextView>(R.id.textNNCount).text = countByPriority("Neither Urgent nor Important").toString()

        val progressBar = findViewById<ProgressBar>(R.id.progressTasks)
        progressBar.max = if (allTasks.isEmpty()) 1 else allTasks.size
        UiAnimator.animateProgress(progressBar, completedCount)

        applyFilters(searchView.query?.toString().orEmpty(), filterSpinner.selectedItem?.toString().orEmpty())
        recyclerView.scheduleLayoutAnimation()
    }

    private fun applyFilters(query: String, filter: String) {
        val filteredTasks = activeTasks.filter { task ->
            val matchesQuery = task.title.contains(query, ignoreCase = true)
            val matchesFilter = filter.isBlank() || filter == "All Priorities" || task.priorityType == filter
            matchesQuery && matchesFilter
        }
        taskAdapter.updateTasks(filteredTasks.take(6))
        emptyStateView.visibility = if (filteredTasks.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun countByPriority(priority: String): Int {
        return activeTasks.count { it.priorityType == priority }
    }

    private fun setupPriorityMatrixClicks() {
        // Each existing matrix card opens the same screen and sends only the selected priority name.
        findViewById<View>(R.id.cardMatrixIU).setOnClickListener {
            openPriorityTasks("Important and Urgent")
        }
        findViewById<View>(R.id.cardMatrixIN).setOnClickListener {
            openPriorityTasks("Important but Not Urgent")
        }
        findViewById<View>(R.id.cardMatrixNU).setOnClickListener {
            openPriorityTasks("Urgent but Not Important")
        }
        findViewById<View>(R.id.cardMatrixNN).setOnClickListener {
            openPriorityTasks("Neither Urgent nor Important")
        }
    }

    private fun openPriorityTasks(priorityType: String) {
        val intent = Intent(this, PriorityTasksActivity::class.java)
        intent.putExtra("priority_type", priorityType)
        startActivity(intent)
    }

    private fun getGreetingMessage(): String {
        // Calendar reads the phone's current system time.
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            currentHour < 12 -> "Good Morning"
            currentHour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
}
