package com.stevennt.movemate.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workouts(
    val name: String,
    val icon: Int,
    val reps: String,
    val instruction: String,
    val focusArea: Int,
) : Parcelable
