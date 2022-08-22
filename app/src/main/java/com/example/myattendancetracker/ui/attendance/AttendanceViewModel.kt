package com.example.myattendancetracker.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.myattendancetracker.db.Attendance
import com.example.myattendancetracker.db.AttendanceData
import com.example.myattendancetracker.db.AttendanceRepository
import kotlinx.coroutines.launch
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    fun insertSubject(attendance: Attendance) = viewModelScope.launch {
        repository.insertSubjectAttendance(attendance)
    }

    fun getALlSubjectAttendance() = liveData {
        repository.attendance.collect {
            emit(it)
        }
    }
    fun getAttendanceData(attendance:Attendance,position: Int)= liveData{
        repository.attendance.collect{
            emit(it[position].attendanceData)
        }
    }
    suspend fun getSubjectAttendance(subjectId: Int): LiveData<Attendance> {
        return repository.getAttendanceRecord(subjectId)
    }

    fun updateAttendanceData(attendance: Attendance) = viewModelScope.launch {
        repository.updateSubjectAttendance(attendance)
    }

    fun deleteAttendanceData(attendanceData: AttendanceData,list: List<AttendanceData>,attendance: Attendance) = viewModelScope.launch {
        val dataList = ArrayList(list)
        dataList.remove(attendanceData)

        attendance.attendanceData = dataList
        attendance.total = dataList.size
        attendance.present = dataList.count { it.present == "P" }
        attendance.absent = dataList.count { it.absent == "A" }
        if(attendance.total!=0)
        attendance.percentage = (attendance.present*100)/attendance.total
        else
            attendance.percentage = 0
        updateAttendanceData(attendance)
    }

    fun deleteSubjectAttendance(attendance: Attendance) = viewModelScope.launch {
        repository.deleteSubjectAttendance(attendance)
    }
}