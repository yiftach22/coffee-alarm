package yiftach.carmon.coffeealarm.alarmTools

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import yiftach.carmon.coffeealarm.*
import java.util.*


class Alarm(
    val alarmId: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    var snoozesLeft: Int,
    var snoozeLength: Int,
    var started: Boolean,
) {

    /**
     * Schedules an alarm to the system and saves it in the SharedPreferences
     */
    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(TITLE, title)
//        intent.putExtra(SNOOZES_LEFT, snoozesLeft)
//        intent.putExtra(SNOOZE_LENGTH, snoozeLength)

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

        val toastText = String.format("Alarm $title scheduled for %02d:%02d", hour, minute)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)
        this.started = true;

        setAlarmOnSp(context)

        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

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


    /**
     * Cancels a set alarm.
     */
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        started = false
        removeAlarmFromSp(context)
        val toastText =
            String.format("Alarm $title cancelled for %02d:%02d", hour, minute)
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }


    /**
     * Sets the alarm to the Shared Preferences.
     */
    private fun setAlarmOnSp(context: Context) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sp.edit()) {
            putInt(ALARM_ID, alarmId)
            putInt(HOUR, hour)
            putInt(MINUTE, minute)
            putBoolean(IS_SET, started)
            putString(TITLE, title)
            putInt(SNOOZES_LEFT, snoozesLeft)
            putInt(SNOOZE_LENGTH, snoozeLength)
            apply()
        }
    }


    companion object{
        /**
         * Removes the alarm from the shared preferences.
         * Works only if there is one alarm at a time in the app.
         */
        fun removeAlarmFromSp(context:Context) {
            val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            with(sp.edit()){
                remove(IS_SET)
                remove(ALARM_ID)
                remove (HOUR)
                remove(MINUTE)
                remove(TITLE)
                remove(SNOOZES_LEFT)
                remove(SNOOZE_LENGTH)
                apply()
            }
        }


    }

}