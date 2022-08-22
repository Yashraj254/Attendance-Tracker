package com.example.myattendancetracker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "attendance_data_table")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name = "_subject")
    var subject: String,
    @ColumnInfo(name = "_data")
    var attendanceData:ArrayList<AttendanceData>,
    @ColumnInfo(name = "_total")
    var total: Int,
    @ColumnInfo(name = "_present")
    var present: Int,
    @ColumnInfo(name = "_absent")
    var absent: Int,
    @ColumnInfo(name = "_percentage")
    var percentage: Int,
):Serializable
