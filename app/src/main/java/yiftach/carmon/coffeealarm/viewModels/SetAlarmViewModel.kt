package yiftach.carmon.coffeealarm.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import yiftach.carmon.coffeealarm.*
import yiftach.carmon.coffeealarm.alarmTools.Alarm

class SetAlarmViewModel: ViewModel() {

    fun setAlarm(context:Context, alarm: Alarm){
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sp.edit()){
            putInt(ALARM_ID, alarm.alarmId)
            putInt(HOUR, alarm.hour)
            putInt(MINUTE, alarm.minute)
            putBoolean(IS_SET, true)
            putString(TITLE, alarm.title)
            putInt(SNOOZES_LEFT, alarm.snoozesLeft)
            putInt(SNOOZE_LENGTH, alarm.snoozeLength)
            apply()
        }

    }
}