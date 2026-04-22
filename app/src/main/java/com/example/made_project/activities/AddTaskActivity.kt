package com.example.made_project.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.made_project.R
import com.example.made_project.database.DatabaseHelper
import com.example.made_project.models.TaskModel
import com.example.made_project.utils.UiAnimator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var editTaskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        databaseHelper = DatabaseHelper(this)
        editTaskId = intent.getIntExtra("task_id", -1)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAddTask)
        toolbar.title = if (editTaskId == -1) "Add Task" else "Edit Task"
        toolbar.setNavigationOnClickListener { finish() }
        UiAnimator.animateSequence(
            toolbar,
            findViewById<View>(R.id.cardTaskForm),
            startDelay = 60L
        )

        val titleInput = findViewById<TextInputEditText>(R.id.editTaskTitle)
        val descriptionInput = findViewById<TextInputEditText>(R.id.editTaskDescription)
        val dueDateInput = findViewById<TextInputEditText>(R.id.editDueDate)
        val prioritySpinner = findViewById<Spinner>(R.id.spinnerPriority)
        val statusSpinner = findViewById<Spinner>(R.id.spinnerStatus)
        val saveButton = findViewById<MaterialButton>(R.id.buttonSaveTask)

        prioritySpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_categories,
            android.R.layout.simple_spinner_dropdown_item
        )

        statusSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.task_statuses,
            android.R.layout.simple_spinner_dropdown_item
        )

        dueDateInput.setOnClickListener {
            showDatePicker(dueDateInput)
        }

        if (editTaskId != -1) {
            populateTaskDetails(titleInput, descriptionInput, dueDateInput, prioritySpinner, statusSpinner)
            saveButton.text = getString(R.string.update_task)
        }

        saveButton.setOnClickListener {
            val title = titleInput.text?.toString()?.trim().orEmpty()
            val description = descriptionInput.text?.toString()?.trim().orEmpty()
            val dueDate = dueDateInput.text?.toString()?.trim().orEmpty()
            val priority = prioritySpinner.selectedItem?.toString().orEmpty()
            val status = statusSpinner.selectedItem?.toString().orEmpty()

            when {
                title.isEmpty() -> titleInput.error = "Task title is required"
                dueDate.isEmpty() -> dueDateInput.error = "Due date is required"
                else -> saveTask(TaskModel(editTaskId, title, description, dueDate, priority, status))
            }
        }

        findViewById<MaterialButton>(R.id.buttonCancelTask).setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(dueDateInput: TextInputEditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val displayDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                dueDateInput.setText(displayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun populateTaskDetails(
        titleInput: TextInputEditText,
        descriptionInput: TextInputEditText,
        dueDateInput: TextInputEditText,
        prioritySpinner: Spinner,
        statusSpinner: Spinner
    ) {
        val task = databaseHelper.getTaskById(editTaskId) ?: return
        titleInput.setText(task.title)
        descriptionInput.setText(task.description)
        dueDateInput.setText(task.dueDate)
        prioritySpinner.setSelection(getSpinnerPosition(prioritySpinner, task.priorityType))
        statusSpinner.setSelection(getSpinnerPosition(statusSpinner, task.status))
    }

    private fun saveTask(task: TaskModel) {
        val isSuccess = if (editTaskId == -1) databaseHelper.insertTask(task) else databaseHelper.updateTask(task)
        if (isSuccess) {
            Snackbar.make(findViewById(android.R.id.content), "Task saved successfully", Snackbar.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Unable to save task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSpinnerPosition(spinner: Spinner, value: String): Int {
        for (index in 0 until spinner.count) {
            if (spinner.getItemAtPosition(index).toString() == value) {
                return index
            }
        }
        return 0
    }
}
