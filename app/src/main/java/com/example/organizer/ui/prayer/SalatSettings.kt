package com.example.organizer.ui.prayer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.databinding.SalatSettingsFragmentBinding
import com.example.organizer.databinding.SalatTimesFragmentBinding
import kotlinx.coroutines.launch

class SalatSettings : Fragment() {

    companion object {
        fun newInstance() = SalatSettings()
    }

    private lateinit var viewModel: SalatSettingsViewModel
    private var _binding: SalatSettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.salat_settings_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProvider(this).get(SalatSettingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val dbInstance = AppDatabase.getInstance(requireContext())
        val salatService = SalatService(
            dbInstance.salatTimesDao(),
            dbInstance.salatSettingsDao(),
            requireContext()
        )
        viewModel.navController = findNavController()
        viewModel.salatService = salatService
        lifecycleScope.launch {
            salatService.getActiveSalatSettings()
        }
        salatService.salatSettingsDAO
            .getActiveSalatSettingsLive()
            .observe(this.viewLifecycleOwner, {
                if (it != null) {
                    viewModel.populateSalatSettings(it)
                }
            })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fajrAlertTime.setStartIconOnClickListener { viewModel.toggleAlertAfterBefore("fajr") }
        binding.dhuhrAlertTime.setStartIconOnClickListener { viewModel.toggleAlertAfterBefore("dhuhr") }
        binding.asrAlertTime.setStartIconOnClickListener { viewModel.toggleAlertAfterBefore("asr") }
        binding.maghribAlertTime.setStartIconOnClickListener { viewModel.toggleAlertAfterBefore("maghrib") }
        binding.ishaAlertTime.setStartIconOnClickListener { viewModel.toggleAlertAfterBefore("isha") }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}