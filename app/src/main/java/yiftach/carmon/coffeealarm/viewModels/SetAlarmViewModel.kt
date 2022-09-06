package yiftach.carmon.coffeealarm.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import yiftach.carmon.coffeealarm.alarmTools.Alarm

class SetAlarmViewModel: ViewModel() {

    fun setAlarm(activity: Activity, alarm: Alarm){
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        with(sp.edit()){
            putInt("alarmId", alarm.alarmId)
            putInt("hour", alarm.hour)
            putInt("minute", alarm.minute)
            putBoolean("isSet", true)
            putString("title", alarm.title)
            putInt("snoozesLeft", alarm.snoozesLeft)
            apply()
        }
    }
}



/* TODO Options:
1. Save alarm only in sharedPreferences.
    Each time the fragment needs info, the viewModel asks the sp.

2. Save the alarm as a variable.
    Each time the SetAlarmViewModel sets an alarm, the currentAlarmViewModel has to be updated too.

3. LiveData. One LiveData to save the data, and update it each time an alarm is set/deleted.
   Has to be only one source, which means an app LiveData or a singleton class.
 */