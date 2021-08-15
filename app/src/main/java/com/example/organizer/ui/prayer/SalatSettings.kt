package com.example.organizer.ui.prayer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.databinding.EditDebtFragmentBinding
import com.example.organizer.databinding.SalatSettingsFragmentBinding
import com.example.organizer.ui.money.debt.editDebt.EditDebtViewModel

class SalatSettings : Fragment() {

    companion object {
        fun newInstance() = SalatSettings()
    }

    private lateinit var viewModel: SalatSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<SalatSettingsFragmentBinding>(
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
        val salatService = SalatService(dbInstance.salatTimesDao(), dbInstance.salatSettingsDao(), requireContext())
        viewModel.navController = findNavController()
        viewModel.salatService = salatService
        salatService.salatSettingsDAO
            .getActiveSalatSettingsLive()
            .observe(this.viewLifecycleOwner, {
                if(it != null) {
                    viewModel.populateSalatSettings(it)
                }
            })
        return view
    }

}