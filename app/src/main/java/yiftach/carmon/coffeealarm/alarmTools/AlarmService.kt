package yiftach.carmon.coffeealarm.alarmTools

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.NotificationCompat
import yiftach.carmon.coffeealarm.CHANNEL_ID
import yiftach.carmon.coffeealarm.R
import yiftach.carmon.coffeealarm.activities.RingActivity


class AlarmService: Service() {



    lateinit var mediaPlayer: MediaPlayer
    lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.isLooping = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, RingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val alarmTitle = "BLABLABLA"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(alarmTitle)
            .setContentText("Ring Ring .. Ring Ring")
            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
            .setContentIntent(pendingIntent)
            .build()

        // start media player
        mediaPlayer.start()

        // start vibration
        val pattern = longArrayOf(0, 100, 1000)
        if (Build.VERSION.SDK_INT >=26){
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else{
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }

        startForeground(1, notification)
        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}