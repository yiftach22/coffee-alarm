package yiftach.carmon.coffeealarm.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel

class RingActivityViewModel: ViewModel() {

    private fun removeAlarmFromSp(activity: Activity){
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        with(sp.edit()){
            putBoolean("isSet", false)
        }

    }
}