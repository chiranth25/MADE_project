package com.example.made_project.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.made_project.R
import com.example.made_project.models.TaskModel

class TaskAdapter(
    private var taskList: List<TaskModel>,
    private val onItemClick: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.textTaskDescription)
        val dueDateText: TextView = itemView.findViewById(R.id.textDueDate)
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
}
