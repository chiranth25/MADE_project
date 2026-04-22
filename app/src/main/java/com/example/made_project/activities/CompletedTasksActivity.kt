package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.made_project.R
import com.example.made_project.adapters.TaskAdapter
import com.example.made_project.database.DatabaseHelper
import com.example.made_project.utils.UiAnimator

class CompletedTasksActivity : BaseActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var emptyStateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        databaseHelper = DatabaseHelper(this)
        emptyStateText = findViewById(R.id.textCompletedEmptyState)

        taskAdapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerCompletedTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
        UiAnimator.animateSequence(
            findViewById(R.id.textCompletedTitle),
            recyclerView,
            startDelay = 80L
        )

        setupBottomNavigation(findViewById(R.id.bottomNavigationCompleted), R.id.nav_completed)
    }

    override fun onResume() {
        super.onResume()
        val completedTasks = databaseHelper.getCompletedTasks()
        taskAdapter.updateTasks(completedTasks)
        emptyStateText.visibility = if (completedTasks.isEmpty()) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.recyclerCompletedTasks).scheduleLayoutAnimation()
    }
}
