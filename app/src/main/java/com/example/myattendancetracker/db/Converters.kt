package com.example.myattendancetracker.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun stringToAttendanceData(json: String?): ArrayList<AttendanceData>? {
        val type: Type = object : TypeToken<ArrayList<AttendanceData?>?>() {}.type
        return Gson().fromJson(json,type)
    }

    @TypeConverter
    fun attendanceToString(list: ArrayList<AttendanceData?>?): String? {
        return Gson().toJson(list)
    }
 @TypeConverter
    fun stringToDates(json: String?): Date? {
        val type: Type = object : TypeToken<Date?>() {}.type
        return Gson().fromJson(json,type)
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return Gson().toJson(date)
    }
}