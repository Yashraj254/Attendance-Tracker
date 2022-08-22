package com.example.myattendancetracker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    /* it is not necessary to insert one student at a time, we can also add a list of students
     * by passing students list as parameter
     */
    @Insert
    suspend fun insertAttendance(attendance: Attendance)

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance_data_table WHERE _id = :id")
     fun getAttendance(id : Int): LiveData<Attendance>

    //as this query returns a liveData, room always run them on background thread by itself so no need of coroutines
    @Query("SELECT * FROM attendance_data_table")
    fun getAllAttendance(): Flow<List<Attendance>>
//  fun getAllAttendances(): LiveData<List<Attendance>>
}