package yiftach.carmon.coffeealarm.fragments

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import yiftach.carmon.coffeealarm.alarmTools.Alarm
import yiftach.carmon.coffeealarm.R
import yiftach.carmon.coffeealarm.viewModels.SetAlarmViewModel
import java.util.*


class SetAlarmFragment : Fragment() {

    lateinit var set_btn: Button
    lateinit var timePicker: TimePicker
    private val setAlarmViewModel: SetAlarmViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_set_alarm, container, false)
        timePicker = view.findViewById(R.id.time_picker)
        timePicker.setIs24HourView(DateFormat.is24HourFormat(activity))
        set_btn = view.findViewById(R.id.set_btn)
        set_btn.setOnClickListener {
            scheduleAlarm()
        }
        return view
    }


    private fun scheduleAlarm() {
        val alarm = Alarm(
            Random().nextInt(Int.MAX_VALUE),
            getHour(timePicker),
            getMinute(timePicker),
            "TEMP_title", //TODO get title
            0, //TODO add snoozes left
            false,
        )
        alarm.schedule(requireContext())
        setAlarmViewModel.setAlarm(requireActivity(), alarm)
        activity?.finish() // TODO navigate back
    }


    private fun getHour(timePicker: TimePicker): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour
        } else {
            @Suppress("DEPRECATION")
            timePicker.currentHour
        }
    }

    private fun getMinute(timePicker: TimePicker): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.minute
        } else {
            @Suppress("DEPRECATION")
            timePicker.currentMinute
        }
    }

}