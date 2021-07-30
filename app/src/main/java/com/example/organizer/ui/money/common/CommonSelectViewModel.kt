package com.example.organizer.ui.money.common

import androidx.lifecycle.ViewModel

abstract class CommonSelectViewModel<T> : ViewModel() {
    var selectedRecord: T? = null
    var selectedRecords = mutableListOf<T>()
    var allSelected = false
    var mode: SELECTION_MODE = SELECTION_MODE.SINGLE
    var currentListIsFiltered = false
    private var currentList = mutableListOf<T>()

    companion object {
        enum class SELECTION_MODE {
            MULTIPLE, SINGLE
        }
    }

    abstract fun areSameRecord(a: T, b: T): Boolean

    fun getSelectedRecordString(emptyString: String, allString:String, stringValueOfRecord: (record: T) -> String): String {
        if(allSelected && !currentListIsFiltered) return allString;
        if(selectedRecords.isNullOrEmpty()) return emptyString
        return selectedRecords.joinToString(",") { r -> stringValueOfRecord(r) }
    }

    fun isRecordSelected(record: T): Boolean {
        return (mode == SELECTION_MODE.SINGLE && selectedRecord != null && areSameRecord(
            selectedRecord!!,
            record
        ))
                || (mode == SELECTION_MODE.MULTIPLE && (allSelected || selectedRecords.find {
            areSameRecord(
                it,
                record
            )
        } != null))
    }

    fun selectRecord(record: T): Boolean {
        var refreshGrid = false
        if (mode == SELECTION_MODE.SINGLE) {
            selectedRecord = record
        } else {
            val existingRecord = selectedRecords.find { areSameRecord(it, record) }
            if (existingRecord == null) {
                selectedRecords.add(record)
                if (selectedRecords.size == currentList.size) {
                    allSelected = true
                    refreshGrid = true
                }
            } else {
                selectedRecords.remove(existingRecord)
                refreshGrid = allSelected
                allSelected = false
            }
        }
        return refreshGrid
    }

    fun selectAll() {
        allSelected = true
        selectedRecords.addAll(currentList);
    }

    fun selectNone() {
        allSelected = false
        selectedRecords.clear()
    }

    fun setCurrentList(list: List<T>, filtered: Boolean = false) {
        currentList = list.toMutableList()
        currentListIsFiltered = filtered
        if (allSelected) {
            selectedRecords.clear()
            selectedRecords.addAll(currentList)
        } else if (filtered) {
            val availableRecords = selectedRecords.filter { s ->
                currentList.find { c ->
                    areSameRecord(
                        s,
                        c
                    )
                } != null
            };
            selectedRecords.clear();
            selectedRecords.addAll(availableRecords);
        }
    }
}