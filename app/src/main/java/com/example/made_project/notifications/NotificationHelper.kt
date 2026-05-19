package com.example.made_project.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.made_project.models.TaskModel
import java.text.SimpleDateFormat
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_ID = "task_reminder_channel"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_DUE_DATE = "extra_due_date"
    const val EXTRA_PRIORITY = "extra_priority"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows local reminders for saved tasks"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskReminder(context: Context, task: TaskModel, reminderDateTime: String): Boolean {
        val triggerTime = parseReminderTimeMillis(reminderDateTime) ?: return false

        // Past alarms are skipped so the app does not show an immediate stale notification.
        if (triggerTime <= System.currentTimeMillis()) {
            return false
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TITLE, task.title)
            putExtra(EXTRA_DUE_DATE, task.dueDate)
            putExtra(EXTRA_PRIORITY, task.priorityType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (task.title + task.dueDate + triggerTime).hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // AlarmManager asks Android to call ReminderReceiver at the selected reminder date and time.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (exception: SecurityException) {
            // If exact alarms are blocked by device settings, use a normal alarm instead of crashing.
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }

        return true
    }

    private fun parseReminderTimeMillis(reminderDateTime: String): Long? {
        return try {
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(reminderDateTime)?.time
        } catch (exception: Exception) {
            null
        }
    }
}
