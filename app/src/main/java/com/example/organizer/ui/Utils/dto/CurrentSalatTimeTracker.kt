package com.example.organizer.ui.Utils.dto

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.example.organizer.database.entity.SalatSettings
import com.example.organizer.database.services.SalatService
import kotlinx.coroutines.launch
import java.util.*

class CurrentSalatTimeTracker (private val salatService: SalatService,
                               private val lifecycleScope: LifecycleCoroutineScope) {
    var map = mutableMapOf<String, SalatDetailedTime>()
    val current = MutableLiveData<SalatDetailedTime.Companion.Event>()
    val nextEvent = MutableLiveData<SalatDetailedTime.Companion.Event>()
    private val delayedCounterExecutionTracker = DelayedCounterExecutionTracker()
    lateinit var salatSettings: SalatSettings
    lateinit var currentSalatTime: SalatDetailedTime
    var handler: Handler? = null
    init {
        lifecycleScope.launch {
            salatSettings = salatService.salatSettingsDAO.getActiveSalatSettings()
            startTracking()
        }
    }

    suspend fun startTracking() {
        val salatTimes = salatService.getConsecutiveSalatTimes(Date(), 2)
        if(salatTimes != null) {
            currentSalatTime = salatTimes[0]
            map = salatTimes.associateBy({ it.salatTime.date }, { it }).toMutableMap()
            val curNext = SalatDetailedTime.getCurrentEventAndPopulate(currentSalatTime, Pair(null, null), true)
            if(curNext.first != null) {
                setCurrentValue(curNext.first!!, false)
                calculateCurrentAndNext(isThread = false, reset = true)
            }
        }
    }

    fun stopHandler() {
        handler?.removeCallbacksAndMessages(null)
    }

    fun setCurrentValue(value: SalatDetailedTime.Companion.Event, isThread: Boolean) {
        if(isThread) {
            current.postValue(value)
        } else {
            current.value = value
        }
    }
    fun setNextValue(value: SalatDetailedTime.Companion.Event, isThread: Boolean) {
        if(isThread) {
            nextEvent.postValue(value)
        } else {
            nextEvent.value = value
        }
    }

    fun calculateCurrentAndNext(isThread: Boolean, reset: Boolean) {
        if(nextEvent.value != null && !reset) {
            setCurrentValue(nextEvent.value!!, isThread)
        }
        if(current.value != null) {
            val nv = SalatDetailedTime.getNextEvent(currentSalatTime, current.value!!)
            if(nv != null) {
                setNextValue(
                    nv,
                    isThread
                )
                startTimer(nv)
            } else {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_MONTH, 1)
                lifecycleScope.launch {
                    val salatTime = salatService.getSalatTimeForDate(cal, salatSettings)
                    if(salatTime != null) {
                        currentSalatTime = SalatDetailedTime(salatTime, salatSettings, currentSalatTime.salatTime)
                        val nv = currentSalatTime.orderedEventList[1]
                        setNextValue(nv, isThread)
                        startTimer(nv)
                    }
                }
            }
        }
    }

    class DelayedCounterExecutionTracker {
        private val tracker = mutableListOf<Long>()

        fun postExecutionTime(time: Date): Boolean {
            if(tracker.size == 5) {
                tracker.removeAt(0)
            }
            tracker.add(time.time)
            return allowedToExecute()
        }

        private fun allowedToExecute(): Boolean {
            return tracker.size < 5 || tracker[4] - tracker[0] > (10 * 60 * 1000)
        }
    }

    private fun startTimer(next: SalatDetailedTime.Companion.Event) {
        val now = Date()
        if(delayedCounterExecutionTracker.postExecutionTime(now)) {
            val nextTimerTime = if (next.range.first == null) next.range.second else nextEvent.value!!.range.first
            if (nextTimerTime != null) {
                val delay = nextTimerTime.time - Date().time
                handler = Handler(Looper.getMainLooper())
                handler!!.postDelayed({
                    calculateCurrentAndNext(true, false)
                }, delay)
            }
        }
    }
}