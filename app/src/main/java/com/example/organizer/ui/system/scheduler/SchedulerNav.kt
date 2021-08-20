package com.example.organizer.ui.system.scheduler

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.organizer.databinding.SchedulerNavFragmentBinding
import com.example.organizer.utils.SalatAlarmUtils
import com.example.organizer.utils.PrefUtils

class SchedulerNav : Fragment() {

    companion object {
        fun newInstance() = SchedulerNav()
    }

    private lateinit var viewModel: SchedulerNavViewModel

    private var _binding: SchedulerNavFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SchedulerNavFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SchedulerNavViewModel::class.java)
        refresh()
    }

    private fun refresh() {
        val context = requireContext()
        binding.scheduleStatus.text = if(PrefUtils.isAlarmSchedulerActive(context)) "ON" else "OFF"
        binding.lastMessage.text = PrefUtils.getAlarmSchedulerLastMessage(context).joinToString("\n\n")
        binding.stopScheduler.setOnClickListener {
            SalatAlarmUtils.stopAlarmScheduler(context, true)
            PrefUtils.setAlarmSchedulerLastMessage(context, "Stop Alarm Scheduler from screen")
        }
        binding.restartScheduler.setOnClickListener { SalatAlarmUtils.startAlarmScheduler(context, true) }
        binding.refresh.setOnClickListener { refresh() }
    }

}