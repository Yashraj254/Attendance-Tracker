package com.example.myattendancetracker.db

import androidx.lifecycle.LiveData

class AttendanceRepository(private val dao: AttendanceDao) {
    val attendance = dao.getAllAttendance()

    suspend fun insertSubjectAttendance(attendance: Attendance) {
        dao.insertAttendance(attendance)
    }

    suspend fun updateSubjectAttendance(attendance: Attendance) {
        dao.updateAttendance(attendance)
    }

    suspend fun deleteSubjectAttendance(attendance: Attendance) {
        dao.deleteAttendance(attendance)
    }

        suspend fun getAttendanceRecord(subjectId: Int): LiveData<Attendance> {
        return  dao.getAttendance(subjectId)
    }

}
