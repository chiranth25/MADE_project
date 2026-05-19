package com.example.made_project.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.made_project.R
import com.example.made_project.activities.DashboardActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE).orEmpty()
        val dueDate = intent.getStringExtra(NotificationHelper.EXTRA_DUE_DATE).orEmpty()
        val priority = intent.getStringExtra(NotificationHelper.EXTRA_PRIORITY).orEmpty()
        val dueTime = getTimeText(dueDate)

        val dashboardIntent = Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // PendingIntent lets the notification open DashboardActivity later when the user taps it.
        val contentIntent = PendingIntent.getActivity(
            context,
            title.hashCode(),
            dashboardIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task_logo)
            .setContentTitle("Reminder: $title at $dueTime")
            .setContentText("Due: $dueDate | $priority")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Due: $dueDate | Priority: $priority"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // NotificationManagerCompat displays the local notification on the device.
        NotificationManagerCompat.from(context).notify(
            (title + dueDate + priority).hashCode(),
            notification
        )
    }

    private fun getTimeText(dateTime: String): String {
        val parts = dateTime.split(" ")
        return if (parts.size >= 3) "${parts[1]} ${parts[2]}" else dateTime
    }
}
