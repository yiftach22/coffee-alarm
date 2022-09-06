package yiftach.carmon.coffeealarm.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import yiftach.carmon.coffeealarm.alarmTools.Alarm


class CurrentAlarmViewModel() : ViewModel() {

    fun getAlarmFromSp(activity: Activity): Alarm? {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        val isSet = sp.getBoolean("isSet", false)
        Log.d("blabla", isSet.toString())
        if (isSet) {
            val alarmId = sp.getInt("alarmId", 0)
            val hour = sp.getInt("hour", 0)
            val minute = sp.getInt("minute", 0)
            val title = sp.getString("title", "Alarm") ?: ""
            val snoozesLeft = sp.getInt("snoozesLeft", 0)
            return Alarm(
                alarmId,
                hour,
                minute,
                title,
                snoozesLeft,
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