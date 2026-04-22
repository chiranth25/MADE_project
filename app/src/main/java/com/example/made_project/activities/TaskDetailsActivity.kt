package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.made_project.R
import com.example.made_project.database.DatabaseHelper
import com.example.made_project.models.TaskModel
import com.example.made_project.utils.UiAnimator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var taskId: Int = -1
    private var taskModel: TaskModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        databaseHelper = DatabaseHelper(this)
        taskId = intent.getIntExtra("task_id", -1)
        UiAnimator.animateSequence(
            findViewById<MaterialToolbar>(R.id.toolbarTaskDetails),
            findViewById<View>(R.id.cardTaskDetails),
            startDelay = 60L
        )

        findViewById<MaterialToolbar>(R.id.toolbarTaskDetails).setNavigationOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.buttonEditTask).setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("task_id", taskId)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.buttonMarkCompleted).setOnClickListener {
            markTaskAsCompleted()
        }

        findViewById<MaterialButton>(R.id.buttonDeleteTask).setOnClickListener {
            showDeleteConfirmation()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTaskDetails()
    }

    private fun loadTaskDetails() {
        taskModel = databaseHelper.getTaskById(taskId)
        val task = taskModel ?: run {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.textDetailTitle).text = task.title
        findViewById<TextView>(R.id.textDetailDescription).text = task.description.ifBlank { "No description available" }
        findViewById<TextView>(R.id.textDetailDueDate).text = task.dueDate
        findViewById<TextView>(R.id.textDetailPriority).text = task.priorityType
        findViewById<TextView>(R.id.textDetailStatus).text = task.status
    }

    private fun markTaskAsCompleted() {
        val task = taskModel ?: return
        val updatedTask = task.copy(status = "Completed")
        if (databaseHelper.updateTask(updatedTask)) {
            Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                if (databaseHelper.deleteTask(taskId)) {
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
