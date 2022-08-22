package com.example.myattendancetracker.db

import androidx.room.PrimaryKey

data class AttendanceData(
    var present: String,
    var absent: String,
    var date: String
)