package com.example.made_project.models

data class TaskModel(
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val priorityType: String,
    val status: String
)
