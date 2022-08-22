package com.example.myattendancetracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Attendance::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AttendanceDatabase : RoomDatabase() {
    //if we had more entity class we would have listed them all here and define references for corresponding DAOs
    abstract fun attendanceDao(): AttendanceDao
    //We should use only one instance of Room Database for the entire app
    //To avoid unexpected error and performance issues we not let multiple instance of database opening at same time
    //so we create a singleton here, in kotlin they are created as companion objects
    companion object {
        @Volatile //Volatile annotation makes the field immediately visible to other threads
        private var INSTANCE: AttendanceDatabase? = null //Reference to Attendance database
        fun getInstance(context: Context): AttendanceDatabase {
            synchronized(this) {    //this represents AttendanceDatabase class
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        AttendanceDatabase::class.java,
                        "attendance_data_database").build()
                }
                return instance
            }
        }
    }
}