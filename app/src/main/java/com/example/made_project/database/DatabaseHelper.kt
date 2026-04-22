package com.example.made_project.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.made_project.models.TaskModel

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DUE_DATE TEXT NOT NULL,
                $COLUMN_PRIORITY_TYPE TEXT NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: TaskModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DUE_DATE, task.dueDate)
            put(COLUMN_PRIORITY_TYPE, task.priorityType)
            put(COLUMN_STATUS, task.status)
        }
        return db.insert(TABLE_TASKS, null, values) != -1L
    }

    fun updateTask(task: TaskModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DUE_DATE, task.dueDate)
            put(COLUMN_PRIORITY_TYPE, task.priorityType)
            put(COLUMN_STATUS, task.status)
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_ID=?", arrayOf(task.id.toString())) > 0
    }

    fun deleteTask(taskId: Int): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID=?", arrayOf(taskId.toString())) > 0
    }

    fun getAllTasks(): List<TaskModel> {
        val taskList = mutableListOf<TaskModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_DUE_DATE ASC", null)

        cursor.use {
            while (it.moveToNext()) {
                taskList.add(
                    TaskModel(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
                        dueDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        priorityType = it.getString(it.getColumnIndexOrThrow(COLUMN_PRIORITY_TYPE)),
                        status = it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS))
                    )
                )
            }
        }
        return taskList
    }

    fun getTaskById(taskId: Int): TaskModel? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ID=?", arrayOf(taskId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return TaskModel(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
                    dueDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                    priorityType = it.getString(it.getColumnIndexOrThrow(COLUMN_PRIORITY_TYPE)),
                    status = it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS))
                )
            }
        }
        return null
    }

    fun getCompletedTasks(): List<TaskModel> {
        return getTasksByStatus("Completed")
    }

    fun getTasksByPriority(priorityType: String): List<TaskModel> {
        val taskList = mutableListOf<TaskModel>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_PRIORITY_TYPE=? ORDER BY $COLUMN_DUE_DATE ASC",
            arrayOf(priorityType)
        )
        cursor.use {
            while (it.moveToNext()) {
                taskList.add(
                    TaskModel(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
                        dueDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        priorityType = it.getString(it.getColumnIndexOrThrow(COLUMN_PRIORITY_TYPE)),
                        status = it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS))
                    )
                )
            }
        }
        return taskList
    }

    fun getTasksByStatus(status: String): List<TaskModel> {
        val taskList = mutableListOf<TaskModel>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_STATUS=? ORDER BY $COLUMN_DUE_DATE ASC",
            arrayOf(status)
        )
        cursor.use {
            while (it.moveToNext()) {
                taskList.add(
                    TaskModel(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
                        dueDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        priorityType = it.getString(it.getColumnIndexOrThrow(COLUMN_PRIORITY_TYPE)),
                        status = it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS))
                    )
                )
            }
        }
        return taskList
    }

    companion object {
        private const val DATABASE_NAME = "TaskManagerDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "Tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DUE_DATE = "dueDate"
        private const val COLUMN_PRIORITY_TYPE = "priorityType"
        private const val COLUMN_STATUS = "status"
    }
}
