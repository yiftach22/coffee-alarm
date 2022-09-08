package yiftach.carmon.coffeealarm.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import yiftach.carmon.coffeealarm.*
import yiftach.carmon.coffeealarm.alarmTools.Alarm


class CurrentAlarmViewModel() : ViewModel() {

    fun getAlarmFromSp(context:Context): Alarm? {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val isSet = sp.getBoolean(IS_SET, false)
        if (isSet) {
            val alarmId = sp.getInt(ALARM_ID, 0)
            val hour = sp.getInt(HOUR, 0)
            val minute = sp.getInt(MINUTE, 0)
            val title = sp.getString(TITLE, "Alarm") ?: ""
            val snoozesLeft = sp.getInt(SNOOZES_LEFT, 0)
            val snoozeLength = sp.getInt(SNOOZE_LENGTH, 2)
            return Alarm(
                alarmId,
                hour,
                minute,
                title,
                snoozesLeft,
                snoozeLength,
                false,
            )
        }
        return null
    }

    fun removeAlarm(activity:Activity) {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        with(sp.edit()){
            putBoolean("isSet", false)
            remove("alarmId")
            remove ("hour")
            remove("minute")
            remove("title")
            remove("snoozesLeft")
            apply()
        }
    }
}