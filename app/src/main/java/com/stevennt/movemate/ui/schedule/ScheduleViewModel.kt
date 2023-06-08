package com.stevennt.movemate.ui.schedule

import androidx.lifecycle.ViewModel
import com.stevennt.movemate.data.MoveMateRepo

class ScheduleViewModel(private val repository: MoveMateRepo) : ViewModel() {
    fun getUserHistory(authToken: String, dateFrom: String, dateTo: String) = repository.getUserHistory(authToken, dateFrom, dateTo)
}