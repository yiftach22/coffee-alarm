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
import yiftach.carmon.coffeealarm.databinding.FragmentSetAlarmBinding
import yiftach.carmon.coffeealarm.viewModels.SetAlarmViewModel
import java.util.*


class SetAlarmFragment : Fragment() {

    private val setAlarmViewModel: SetAlarmViewModel by activityViewModels()

    private var _binding: FragmentSetAlarmBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSetAlarmBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        binding.timePicker.setIs24HourView(DateFormat.is24HourFormat(activity))

        // Set snooze number picker
        binding.snoozeNp.maxValue = 10
        binding.snoozeNp.minValue = 0
        binding.snoozeNp.value = 3

        binding.setBtn.setOnClickListener {
            scheduleAlarm()
            requireActivity().onBackPressed()
        }
        return binding.root
    }


    private fun scheduleAlarm() {
        val alarm = Alarm(
            Random().nextInt(Int.MAX_VALUE),
            getHour(binding.timePicker),
            getMinute(binding.timePicker),
            binding.titleEt.text.toString(),
            binding.snoozeNp.value,
            false,
        )
        alarm.schedule(requireContext())
        setAlarmViewModel.setAlarm(requireActivity(), alarm)
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