package com.example.made_project.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.made_project.R
import com.example.made_project.models.TaskModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskAdapter(
    private var taskList: List<TaskModel>,
    private val onItemClick: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.textTaskDescription)
        val dueDateText: TextView = itemView.findViewById(R.id.textDueDate)
        val dueWarningText: TextView = itemView.findViewById(R.id.textDueWarning)
        val priorityText: TextView = itemView.findViewById(R.id.textPriority)
        val statusText: TextView = itemView.findViewById(R.id.textStatus)
        val cardView: CardView = itemView.findViewById(R.id.taskCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.titleText.text = task.title
        holder.descriptionText.text = if (task.description.isBlank()) "No description added" else task.description
        holder.dueDateText.text = "Due: ${task.dueDate}"
        holder.priorityText.text = task.priorityType
        holder.priorityText.background = createPriorityBadgeBackground(getPriorityColor(task.priorityType))
        showDueDateWarning(holder.dueWarningText, task.dueDate)
        holder.statusText.text = task.status
        holder.statusText.setTextColor(getStatusColor(task.status))

        holder.cardView.setOnClickListener {
            onItemClick(task)
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(updatedList: List<TaskModel>) {
        taskList = updatedList
        notifyDataSetChanged()
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "Completed" -> Color.parseColor("#2BB673")
            "In Progress" -> Color.parseColor("#7A4DFF")
            else -> Color.parseColor("#F05365")
        }
    }

    private fun getPriorityColor(priorityType: String): Int {
        // when is clear for viva explanation because each priority name maps to one fixed color.
        return when (priorityType) {
            "Important and Urgent" -> Color.parseColor("#F05365")
            "Important but Not Urgent" -> Color.parseColor("#1F3C88")
            "Urgent but Not Important" -> Color.parseColor("#FF9800")
            "Neither Urgent nor Important" -> Color.parseColor("#8E97A8")
            else -> Color.parseColor("#8E97A8")
        }
    }

    private fun createPriorityBadgeBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 24f
            setColor(color)
        }
    }

    private fun showDueDateWarning(warningText: TextView, dueDate: String) {
        val taskDate = parseDateOnly(dueDate)
        if (taskDate == null) {
            warningText.visibility = View.GONE
            return
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val taskDay = Calendar.getInstance().apply {
            time = taskDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Dates are compared after removing time so only the calendar day matters.
        when {
            taskDay.before(today) -> {
                warningText.text = "Overdue"
                warningText.setTextColor(Color.parseColor("#F05365"))
                warningText.visibility = View.VISIBLE
            }
            taskDay == today -> {
                warningText.text = "Due Today"
                warningText.setTextColor(Color.parseColor("#FFB547"))
                warningText.visibility = View.VISIBLE
            }
            else -> warningText.visibility = View.GONE
        }
    }

    private fun parseDateOnly(dueDate: String) = try {
        val datePattern = if (dueDate.contains(":")) "dd/MM/yyyy hh:mm a" else "dd/MM/yyyy"
        SimpleDateFormat(datePattern, Locale.getDefault()).parse(dueDate)
    } catch (exception: Exception) {
        null
    }
}
