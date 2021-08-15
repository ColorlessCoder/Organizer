package com.example.organizer.ui.prayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.databinding.SalatEventRowBinding
import com.example.organizer.databinding.SalatEventsByDateBinding
import com.example.organizer.databinding.SalatTimesFragmentBinding
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.dto.SalatDetailedTime
import kotlinx.coroutines.launch
import java.util.*

class SalatTimesFragment : Fragment() {

    companion object {
        fun newInstance() = SalatTimesFragment()
    }

    private lateinit var viewModel: SalatTimesViewModel
    private var _binding: SalatTimesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SalatTimesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SalatTimesViewModel::class.java)
        val context = requireContext()
        val db = AppDatabase.getInstance(context)
        val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
        lifecycleScope.launch {
            val detailedTimes = salatService.getConsecutiveSalatTimes(Date(), 7)
            if(detailedTimes != null) {
                val map = detailedTimes.associateBy( {it.salatTime.date}, {it} )
                SalatDetailedTime.getCurrentEventAndPopulate(map)
                binding.eventsByDate.adapter = SalatDateListAdapter(detailedTimes)
            }
        }
    }

    class SalatDateListAdapter(
        private val salatDetailedTimeList: List<SalatDetailedTime>
    ) : RecyclerView.Adapter<SalatDateListAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding = SalatEventsByDateBinding.bind(view)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.salat_events_by_date, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return salatDetailedTimeList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val binding = holder.binding
            val salatDetailedTime = salatDetailedTimeList[position]
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DATE, 1)
            if (salatDetailedTime.salatTime.date == DateUtils.serializeSalatDate(Date())) {
                binding.headingExtra.visibility = View.VISIBLE
                binding.headingExtra.setText(R.string.today)
            } else if (salatDetailedTime.salatTime.date == DateUtils.serializeSalatDate(tomorrow.time)) {
                binding.headingExtra.visibility = View.VISIBLE
                binding.headingExtra.setText(R.string.tomorrow)
            } else {
                binding.headingExtra.visibility = View.GONE
            }
            binding.headingDate.text = DateUtils.salatDisplayDate(salatDetailedTime.salatTime.date)
            binding.eventsForSingleDay.adapter = SalatEventListAdapter(salatDetailedTime.orderedEventList.filter { it.status != SalatDetailedTime.Companion.Status.IGNORE })
        }

    }

    class SalatEventListAdapter(
        private val eventList: List<SalatDetailedTime.Companion.Event>
    ) : RecyclerView.Adapter<SalatEventListAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding = SalatEventRowBinding.bind(view)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.salat_event_row, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return eventList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val binding = holder.binding
            val event = eventList[position]
            binding.eventName.setText(event.type.labelKey)
            if (event.status != null) {
                binding.eventStatus.visibility = View.VISIBLE
                binding.eventStatus.setText(event.status!!.labelKey)
            } else {
                binding.eventStatus.visibility = View.GONE
            }
            binding.eventCard.strokeWidth =
                if (event.status == SalatDetailedTime.Companion.Status.ONGOING) 2 else 0

            binding.extraDateSection.visibility = View.GONE

            if(SalatDetailedTime.redZones.contains(event.type)) {
                binding.eventCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.RedShadowColor))
            }

            if(event.range.first != null) {
                binding.startAt.visibility = View.VISIBLE
                binding.startAtTime.visibility = View.VISIBLE
                binding.startAtTime.text = DateUtils.salatDisplayTime(event.range.first!!)
                if(DateUtils.serializeSalatDate(event.range.first!!) != event.date) {
                    binding.startExtraDate.text = DateUtils.salatDisplayDate(event.range.first!!)
                    binding.extraDateSection.visibility = View.VISIBLE
                } else {
                    binding.startExtraDate.text = null
                }
            } else {
                binding.startAt.visibility = View.GONE
                binding.startAtTime.visibility = View.GONE
            }
            if(event.range.second != null) {
                binding.endAt.visibility = View.VISIBLE
                binding.endAtTime.visibility = View.VISIBLE
                binding.endAtTime.text = DateUtils.salatDisplayTime(event.range.second!!)
                if(DateUtils.serializeSalatDate(event.range.second!!) != event.date) {
                    binding.endExtraDate.text = DateUtils.salatDisplayDate(event.range.second!!)
                    binding.extraDateSection.visibility = View.VISIBLE
                } else {
                    binding.endExtraDate.text = null
                }
            } else {
                binding.endAt.visibility = View.GONE
                binding.endAtTime.visibility = View.GONE
            }
        }

    }
}