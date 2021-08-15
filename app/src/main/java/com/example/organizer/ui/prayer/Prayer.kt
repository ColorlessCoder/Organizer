package com.example.organizer.ui.prayer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.databinding.PrayerFragmentBinding
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.dto.SalatDetailedTime
import kotlinx.coroutines.launch

class Prayer : Fragment() {

    companion object {
        fun newInstance() = Prayer()
    }

    private var _binding: PrayerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PrayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PrayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setBanner(curNextAddress: Pair<Pair<SalatDetailedTime.Companion.Event?, SalatDetailedTime.Companion.Event?>, String>) {
        val banner = binding.banner
        val current = curNextAddress.first.first
        if (current != null) {
            banner.address.text = curNextAddress.second.split(",")[0]
            banner.headerDate.text = DateUtils.salatDisplayDate(current.date)
            if (current.status != null) {
                banner.status.setText(current.status!!.labelKey)
            }
            banner.eventName.setText(current.type.labelKey)
            if (current.range.first != null) {
                banner.startTime.visibility = View.VISIBLE
                banner.startTime.text = DateUtils.salatDisplayTime(current.range.first!!)
            } else {
                banner.startTime.visibility = View.GONE
            }
            if (current.range.second != null) {
                banner.endSection.visibility = View.VISIBLE
                banner.endTimeAt.text = DateUtils.salatDisplayTime(current.range.second!!)
            } else {
                banner.endSection.visibility = View.GONE
            }
        }
        val next =  curNextAddress.first.second
        if (next != null) {
            banner.nextEventName.setText(next.type.labelKey)
            banner.nextSection.visibility = View.VISIBLE
        } else {
            banner.nextSection.visibility = View.GONE
        }
    }

    private fun navigateToSalatTimeList() {
        val action = PrayerDirections.actionNavPrayerToSalatTimesFragment()
        findNavController().navigate(action)
    }
    private fun navigateToSettings() {
        val action = PrayerDirections.actionNavPrayerToSalatSettings()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrayerViewModel::class.java)
        binding.prayerTime.setOnClickListener { navigateToSalatTimeList() }
        binding.banner.nextSection.setOnClickListener { navigateToSalatTimeList() }
        binding.salatSettings.setOnClickListener { navigateToSettings() }
        val context = requireContext()
        val db = AppDatabase.getInstance(context)
        val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
        lifecycleScope.launch {
            val curNext = salatService.getCurrentAndNextEvent()
            setBanner(curNext)
        }
    }

}