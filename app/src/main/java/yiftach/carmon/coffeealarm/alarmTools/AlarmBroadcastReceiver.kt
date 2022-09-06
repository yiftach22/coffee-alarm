package yiftach.carmon.coffeealarm.alarmTools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import yiftach.carmon.coffeealarm.TITLE


class AlarmBroadcastReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null)
            return
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.action)){
            val toastText = "Alarm Reboot"
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            startRescheduleAlarmService(context)
        }
        else{
            val toastText = "Alarm Received"
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            startAlarmService(context, intent)
        }

    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intentService)
        } else{
            context.startService(intentService)
        }
    }

    private fun startRescheduleAlarmService(context: Context) {

    }
}