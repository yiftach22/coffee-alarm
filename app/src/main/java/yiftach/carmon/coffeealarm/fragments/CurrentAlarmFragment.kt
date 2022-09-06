package yiftach.carmon.coffeealarm.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import yiftach.carmon.coffeealarm.alarmTools.Alarm
import yiftach.carmon.coffeealarm.viewModels.CurrentAlarmViewModel
import yiftach.carmon.coffeealarm.R
import yiftach.carmon.coffeealarm.databinding.FragmentCurrentAlarmBinding


class CurrentAlarmFragment : Fragment() {

    private var activeAlarm: Alarm? = null
    private lateinit var sp: SharedPreferences
    private val currentAlarmViewModel: CurrentAlarmViewModel by viewModels()

    // binding
    private var _binding: FragmentCurrentAlarmBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentAlarmBinding.inflate(inflater, container, false)
        activeAlarm = currentAlarmViewModel.getAlarmFromSp(requireActivity())
        if (activeAlarm == null) {
            binding.currentAlarmLayout.visibility = View.GONE
            binding.noAlarmLayout.visibility = View.VISIBLE
            binding.setAlarmBtn.setOnClickListener {
                binding.root.findNavController()
                    .navigate(R.id.action_currentAlarmFragment_to_setAlarmFragment)
            }
        } else {
            setAlarmView()
        }
        return binding.root
    }


    private fun setAlarmView() {

        binding.currentAlarmLayout.visibility = View.VISIBLE
        binding.noAlarmLayout.visibility = View.GONE


        val is24 = is24HourFormat(activity)
        var hours = activeAlarm!!.hour
        if (is24) {
            binding.ampmTv.visibility = View.GONE
        } else {
            binding.ampmTv.visibility = View.VISIBLE
            if (hours < 12) {
                binding.ampmTv.text = "AM"
            } else {
                binding.ampmTv.text = "PM"
                hours %= 12
            }
            if (hours == 0) {
                hours = 12
            }
        }

        binding.alarmTimeTv.text = String.format("%02d:%02d", hours, activeAlarm!!.minute)
        binding.alarmTitleTv.text = activeAlarm!!.title
        binding.snoozeNumberTv.text = activeAlarm!!.snoozesLeft.toString()


    }
}