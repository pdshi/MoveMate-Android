package com.stevennt.movemate.ui.bio

import androidx.lifecycle.ViewModel
import com.stevennt.movemate.data.MoveMateRepo
import com.stevennt.movemate.data.model.*

class BioViewModel(private val repository: MoveMateRepo) : ViewModel() {
    fun inputUserData(
        authToken: String,
        displayName: String,
        photoUrl: String,
        gender: String,
        age: String,
        height: String,
        weight: String,
        goal: String,
        goalWeight: String,
        frequency: String,
        dayStart: String,
        woTime: String,
    ) = repository.inputUserData(
        authToken,
        displayName,
        photoUrl,
        gender,
        age,
        height,
        weight,
        goal,
        goalWeight,
        frequency,
        dayStart,
        woTime,
    )

    fun inputUserReps(
        authToken: String,
        type: String,
        reps: String,
        sets: String,
        date: String,
        start: String,
        end: String
    ) = repository.inputUserReps(authToken, type, reps, sets, date, start, end)
}