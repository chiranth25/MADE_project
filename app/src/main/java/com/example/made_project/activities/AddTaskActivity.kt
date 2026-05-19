package com.example.made_project.activities

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.made_project.R
import com.example.made_project.database.DatabaseHelper
import com.example.made_project.models.TaskModel
import com.example.made_project.notifications.NotificationHelper
import com.example.made_project.utils.UiAnimator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var editTaskId: Int = -1
    private var selectedDueCalendar: Calendar? = null
    private var selectedReminderCalendar: Calendar? = null

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        databaseHelper = DatabaseHelper(this)
        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermissionIfNeeded()
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
        val dueTimeInput = findViewById<TextInputEditText>(R.id.editDueTime)
        val prioritySpinner = findViewById<Spinner>(R.id.spinnerPriority)
        val statusSpinner = findViewById<Spinner>(R.id.spinnerStatus)
        val reminderDateInput = findViewById<TextInputEditText>(R.id.editReminderDate)
        val reminderTimeInput = findViewById<TextInputEditText>(R.id.editReminderTime)
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
        dueTimeInput.setOnClickListener {
            showTimePicker(dueTimeInput)
        }
        reminderDateInput.setOnClickListener {
            showDatePicker(reminderDateInput)
        }
        reminderTimeInput.setOnClickListener {
            showTimePicker(reminderTimeInput)
        }

        if (editTaskId != -1) {
            populateTaskDetails(
                titleInput,
                descriptionInput,
                dueDateInput,
                dueTimeInput,
                reminderDateInput,
                reminderTimeInput,
                prioritySpinner,
                statusSpinner
            )
            saveButton.text = getString(R.string.update_task)
        }

        saveButton.setOnClickListener {
            val title = titleInput.text?.toString()?.trim().orEmpty()
            val description = descriptionInput.text?.toString()?.trim().orEmpty()
            val dueDate = dueDateInput.text?.toString()?.trim().orEmpty()
            val dueTime = dueTimeInput.text?.toString()?.trim().orEmpty()
            val reminderDate = reminderDateInput.text?.toString()?.trim().orEmpty()
            val reminderTime = reminderTimeInput.text?.toString()?.trim().orEmpty()
            val priority = prioritySpinner.selectedItem?.toString().orEmpty()
            val status = statusSpinner.selectedItem?.toString().orEmpty()

            when {
                title.isEmpty() -> titleInput.error = "Task title is required"
                dueDate.isEmpty() -> dueDateInput.error = "Due date is required"
                dueTime.isEmpty() -> dueTimeInput.error = "Due time is required"
                reminderDate.isEmpty() -> reminderDateInput.error = "Reminder date is required"
                reminderTime.isEmpty() -> reminderTimeInput.error = "Reminder time is required"
                else -> {
                    // Date and time are combined into one String and saved in the existing dueDate column.
                    val dueDateTime = "$dueDate $dueTime"
                    val reminderDateTime = "$reminderDate $reminderTime"
                    saveTask(TaskModel(editTaskId, title, description, dueDateTime, priority, status), reminderDateTime)
                }
            }
        }

        findViewById<MaterialButton>(R.id.buttonCancelTask).setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(input: TextInputEditText) {
        val calendar = getCalendarForInput(input)
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = getCalendarForInput(input)
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Date is shown separately in the form for easy selection.
                input.setText(dateFormatter.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(input: TextInputEditText) {
        val calendar = getCalendarForInput(input)
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedCalendar = getCalendarForInput(input)
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                // TimePickerDialog gives hour/minute values; formatter displays them as AM/PM text.
                input.setText(timeFormatter.format(selectedCalendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun populateTaskDetails(
        titleInput: TextInputEditText,
        descriptionInput: TextInputEditText,
        dueDateInput: TextInputEditText,
        dueTimeInput: TextInputEditText,
        reminderDateInput: TextInputEditText,
        reminderTimeInput: TextInputEditText,
        prioritySpinner: Spinner,
        statusSpinner: Spinner
    ) {
        val task = databaseHelper.getTaskById(editTaskId) ?: return
        titleInput.setText(task.title)
        descriptionInput.setText(task.description)

        selectedDueCalendar = parseDateTime(task.dueDate) ?: parseOldDateOnly(task.dueDate)
        selectedDueCalendar?.let { calendar ->
            dueDateInput.setText(dateFormatter.format(calendar.time))
            dueTimeInput.setText(timeFormatter.format(calendar.time))

            // Reminder date-time is not stored in SQLite, so edit mode uses a beginner-friendly default.
            selectedReminderCalendar = calendar.clone() as Calendar
            selectedReminderCalendar?.add(Calendar.MINUTE, -10)
            selectedReminderCalendar?.let { reminderCalendar ->
                reminderDateInput.setText(dateFormatter.format(reminderCalendar.time))
                reminderTimeInput.setText(timeFormatter.format(reminderCalendar.time))
            }
        } ?: dueDateInput.setText(task.dueDate)

        prioritySpinner.setSelection(getSpinnerPosition(prioritySpinner, task.priorityType))
        statusSpinner.setSelection(getSpinnerPosition(statusSpinner, task.status))
    }

    private fun saveTask(task: TaskModel, reminderDateTime: String) {
        val isSuccess = if (editTaskId == -1) databaseHelper.insertTask(task) else databaseHelper.updateTask(task)
        if (isSuccess) {
            // Reminder date-time stays separate from SQLite and is only used for AlarmManager.
            NotificationHelper.scheduleTaskReminder(this, task, reminderDateTime)
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

    private fun getCalendarForInput(input: TextInputEditText): Calendar {
        val existingCalendar = when (input.id) {
            R.id.editDueDate, R.id.editDueTime -> selectedDueCalendar
            R.id.editReminderDate, R.id.editReminderTime -> selectedReminderCalendar
            else -> null
        }

        val calendar = existingCalendar ?: Calendar.getInstance()
        when (input.id) {
            R.id.editDueDate, R.id.editDueTime -> selectedDueCalendar = calendar
            R.id.editReminderDate, R.id.editReminderTime -> selectedReminderCalendar = calendar
        }
        return calendar
    }

    private fun parseDateTime(dateTime: String): Calendar? {
        return try {
            Calendar.getInstance().apply {
                time = dateTimeFormatter.parse(dateTime) ?: return null
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun parseOldDateOnly(date: String): Calendar? {
        return try {
            Calendar.getInstance().apply {
                time = dateFormatter.parse(date) ?: return null
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
