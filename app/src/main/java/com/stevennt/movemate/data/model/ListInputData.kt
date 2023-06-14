package com.stevennt.movemate.data.model

data class ListInputData(
    var displayName: String,
    var photoUrl: String,
    var gender: String,
    var age: String,
    var height: String,
    var weight: String,
    var goal: String,
    var goalWeight: String,
    var frequency: String,
    var dayStart: String,
    var woTime: String,
    var createdAt: String,
    var updatedAt: String
)

var displayName = ""
var photoUrl = ""
var gender = ""
var age = ""
var height = ""
var weight = ""
var goal = ""
var goalWeight = ""
var frequency = ""
var dayStart = ""
var woTime = ""
var createdAt = ""
var updatedAt = ""

var userDataArray = arrayOf(
    ListInputData(
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
        createdAt,
        updatedAt
    )
)
