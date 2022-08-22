package com.example.myattendancetracker.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendancetracker.R
import com.example.myattendancetracker.databinding.DatesItemBinding
import com.example.myattendancetracker.db.Attendance
import com.example.myattendancetracker.db.AttendanceData
import com.example.myattendancetracker.ui.attendance.AttendanceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AttendanceDataRecyclerViewAdapter(
    private val tvStatus: TextView,
    private val fabDate: Button,
    private val attendance: Attendance,
    private val fabSave: Button,
    private val viewModel: AttendanceViewModel,
) :
    RecyclerView.Adapter<AttendanceDataViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<AttendanceData>() {
        override fun areItemsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, callback)

    private val dateList = ArrayList<AttendanceData>()
    var totalPresent = 0
    var totalAbsent = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceDataViewHolder {
        val binding: DatesItemBinding =
            DatesItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)

       // Log.i("Mytag", "onCreateViewHolder: Present: $totalPresent Absent:$totalAbsent")
        return AttendanceDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceDataViewHolder, position: Int) {
        totalPresent = attendance.present
        totalAbsent = attendance.absent
        holder.bind(differ.currentList[position])
        //  Log.i("Mytag", "${differ.currentList} ")

        val rbPresent = holder.itemView.findViewById<RadioButton>(R.id.rbPresent)
        val rbAbsent = holder.itemView.findViewById<RadioButton>(R.id.rbAbsent)
        val rgStatus = holder.itemView.findViewById<RadioGroup>(R.id.rgStatus)
        val ivPresent = holder.itemView.findViewById<ImageView>(R.id.ivPresent)
        val ivAbsent = holder.itemView.findViewById<ImageView>(R.id.ivAbsent)


        var percentage = 0

        fabSave.setOnClickListener {

            if (rbPresent.isChecked) {
                totalPresent++
                rgStatus.visibility = View.GONE
                ivPresent.visibility = View.VISIBLE
                ivAbsent.visibility = View.INVISIBLE
                differ.currentList.get(position).present = "P"
            }
            if (rbAbsent.isChecked) {
                totalAbsent++
                rgStatus.visibility = View.GONE
                ivAbsent.visibility = View.VISIBLE
                ivPresent.visibility = View.INVISIBLE
                differ.currentList.get(position).absent = "A"
            }
            if (differ.currentList.size != 0)
                percentage = (totalPresent * 100) / differ.currentList.size


            viewModel.updateAttendanceData(Attendance(attendance.id,
                attendance.subject,
                ArrayList(differ.currentList),
                differ.currentList.size,
                totalPresent,
                totalAbsent,
                percentage))

            if (percentage < 75) {
                val classesRequired =
                    (((0.75 * differ.currentList.size) - totalPresent) / 0.25).toInt()
                tvStatus.text =
                    "You have to attend $classesRequired more classes to maintain 75% attendance"
            } else {
                tvStatus.text = "Your attendance is above 75%. Keep up!!"
            }


            fabSave.visibility = View.GONE
            fabDate.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setList(date: List<AttendanceData>) {
        dateList.clear()
        dateList.addAll(date)
    }
}

class AttendanceDataViewHolder(private val binding: DatesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(attendanceData: AttendanceData) {

        binding.apply {
            tvDate.text = attendanceData.date
            if (attendanceData.present == "P") {
                rgStatus.visibility = View.GONE
                ivPresent.visibility = View.VISIBLE
                ivAbsent.visibility = View.INVISIBLE
            } else
                if (attendanceData.absent == "A") {
                    rgStatus.visibility = View.GONE
                    ivAbsent.visibility = View.VISIBLE
                    ivPresent.visibility = View.INVISIBLE
                } else {
                    rgStatus.visibility = View.VISIBLE
                    ivAbsent.visibility = View.GONE
                    ivPresent.visibility = View.GONE
                }
        }
    }
}