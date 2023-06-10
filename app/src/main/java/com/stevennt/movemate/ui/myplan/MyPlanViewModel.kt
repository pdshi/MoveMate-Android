package com.stevennt.movemate.ui.myplan

import androidx.lifecycle.ViewModel
import com.stevennt.movemate.data.MoveMateRepo

class MyPlanViewModel(private val repository: MoveMateRepo) : ViewModel() {
    fun getUserReps(authToken: String, currentDate: String) = repository.getUserReps(authToken, currentDate)
}