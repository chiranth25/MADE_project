package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.made_project.R
import com.example.made_project.adapters.TaskAdapter
import com.example.made_project.database.DatabaseHelper
import com.google.android.material.appbar.MaterialToolbar

class PriorityTasksActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var emptyStateText: TextView
    private lateinit var priorityType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priority_tasks)

        databaseHelper = DatabaseHelper(this)
        priorityType = intent.getStringExtra("priority_type").orEmpty()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarPriorityTasks)
        toolbar.title = priorityType.ifBlank { "Priority Tasks" }
        toolbar.setNavigationOnClickListener { finish() }

        emptyStateText = findViewById(R.id.textPriorityEmptyState)

        taskAdapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerPriorityTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }

    override fun onResume() {
        super.onResume()
        loadPriorityTasks()
    }

    private fun loadPriorityTasks() {
        // DatabaseHelper uses a WHERE query, so only tasks from the selected matrix category are loaded.
        val tasks = if (priorityType.isBlank()) {
            emptyList()
        } else {
            databaseHelper.getTasksByPriority(priorityType)
        }

        taskAdapter.updateTasks(tasks)
        emptyStateText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    }
}
