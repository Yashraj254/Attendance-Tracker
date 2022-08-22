package com.example.myattendancetracker.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendancetracker.databinding.AttendanceItemBinding
import com.example.myattendancetracker.db.Attendance
import com.example.myattendancetracker.db.AttendanceData

class AttendanceRecyclerViewAdapter:
    RecyclerView.Adapter<AttendanceRecyclerViewAdapter.AttendanceViewHolder>() {

    private val callback = object: DiffUtil.ItemCallback<Attendance>(){
        override fun areItemsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,callback)

   inner class AttendanceViewHolder(private val binding: AttendanceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            Log.i("MyTag", "onBindViewHolder: $attendance")
            binding.apply {
                tvSubject.text = attendance.subject
                tvPresent.text = "Present: ${attendance.present}"
                tvAbsent.text = "Absent: ${attendance.absent}"
                tvTotal.text = "Total: ${attendance.total}"
                pbPercentage.progress = attendance.percentage
                tvPercentage.text = "${attendance.percentage}%"
                //   tvPresent.text = attendance.attendanceData.get(position).present.toString()
                root.setOnClickListener { onItemClickListener(attendance) }
            }
        }
    }
    private val attendanceList = ArrayList<Attendance>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding: AttendanceItemBinding =
            AttendanceItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(differ.currentList[position])

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private lateinit var onItemClickListener : (Attendance)->Unit
    fun setOnItemClickListener(listener:(Attendance)->Unit){
        onItemClickListener = listener
    }
}

