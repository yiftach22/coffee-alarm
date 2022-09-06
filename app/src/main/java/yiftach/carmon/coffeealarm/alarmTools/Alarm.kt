package yiftach.carmon.coffeealarm.alarmTools

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*



class Alarm(
    val alarmId: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    var snoozesLeft: Int,
    var started: Boolean,
) {

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(TITLE, title)

        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }

        val toastText = "One Time Alarm $title scheduled for ${getDayName(calendar)}"
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        this.started = true;
    }

    private fun getDayName(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            else -> "Saturday"
        }
    }
}