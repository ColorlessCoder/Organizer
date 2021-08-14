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
import com.example.organizer.ui.Utils.dto.CurrentSalatTimeTracker
import kotlinx.coroutines.launch

class Prayer : Fragment() {

    companion object {
        fun newInstance() = Prayer()
    }

    private var _binding: PrayerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PrayerViewModel
    private var salatTracker: CurrentSalatTimeTracker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PrayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        salatTracker?.stopHandler()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        salatTracker?.stopHandler()
        super.onDestroy()
    }

    override fun onPause() {
        salatTracker?.stopHandler()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            salatTracker?.startTracking()
        }
    }

    private fun setBanner(salatService: SalatService) {
        salatTracker = CurrentSalatTimeTracker(salatService, lifecycleScope)
        val banner = binding.banner
        salatTracker!!.current.observe(this.viewLifecycleOwner, {
            if (it != null) {
                if(salatTracker!!.salatSettings.address != null) {
                    banner.address.text = salatTracker!!.salatSettings.address!!.split(",")[0]
                } else {
                    banner.address.text = ""
                }
                banner.headerDate.text = DateUtils.salatDisplayDate(it.date)
                if (it.status != null) {
                    banner.status.setText(it.status!!.labelKey)
                }
                banner.eventName.setText(it.type.labelKey)
                if (it.range.first != null) {
                    banner.startTime.visibility = View.VISIBLE
                    banner.startTime.text = DateUtils.salatDisplayTime(it.range.first!!)
                } else {
                    banner.startTime.visibility = View.GONE
                }
                if (it.range.second != null) {
                    banner.endSection.visibility = View.VISIBLE
                    banner.endTimeAt.text = DateUtils.salatDisplayTime(it.range.second!!)
                } else {
                    banner.endSection.visibility = View.GONE
                }
            }
        })
        salatTracker!!.nextEvent.observe(this.viewLifecycleOwner, {
            if (it != null) {
                if (salatTracker!!.nextEvent.value != null) {
                    banner.nextEventName.setText(it.type.labelKey)
                    banner.nextSection.visibility = View.VISIBLE
                } else {
                    banner.nextSection.visibility = View.GONE
                }
            }
        })
    }

    private fun navigateToSalatTimeList() {
        val action = PrayerDirections.actionNavPrayerToSalatTimesFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrayerViewModel::class.java)
        binding.prayerTime.setOnClickListener { navigateToSalatTimeList() }
        binding.banner.nextSection.setOnClickListener { navigateToSalatTimeList() }
        val context = requireContext()
        val db = AppDatabase.getInstance(context)
        val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
        setBanner(salatService)
    }

}