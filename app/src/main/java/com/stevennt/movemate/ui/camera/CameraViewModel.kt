package com.stevennt.movemate.ui.camera

import androidx.lifecycle.ViewModel
import com.stevennt.movemate.data.MoveMateRepo

class CameraViewModel(private val repository: MoveMateRepo) : ViewModel() {
    fun inputUserHistory(authToken: String, type: String, time: Int, reps: Int) = repository.inputUserHistory(authToken, type, time, reps)
}