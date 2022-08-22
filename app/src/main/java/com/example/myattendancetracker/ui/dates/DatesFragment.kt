package com.example.myattendancetracker.ui.dates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendancetracker.databinding.FragmentDatesBinding
import com.example.myattendancetracker.db.Attendance
import com.example.myattendancetracker.db.AttendanceData
import com.example.myattendancetracker.db.AttendanceDatabase
import com.example.myattendancetracker.db.AttendanceRepository
import com.example.myattendancetracker.ui.adapters.AttendanceDataRecyclerViewAdapter
import com.example.myattendancetracker.ui.attendance.AttendanceViewModel
import com.example.myattendancetracker.ui.attendance.AttendanceViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class DatesFragment : Fragment() {

    private lateinit var binding: FragmentDatesBinding
    private lateinit var rvAdapter: AttendanceDataRecyclerViewAdapter
    var present = 0
    var absent = 0
    private var swipeEnabled = true
    private var canScroll = true
    private var total = 0
    private var position: Int = 0
    private var percentage: Int = 0
    private lateinit var attendance: Attendance
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(com.example.myattendancetracker.R.layout.fragment_dates,
            container,
            false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDatesBinding.bind(view)
        val dao = context?.let {
            AttendanceDatabase.getInstance(requireContext()).attendanceDao()
        }
        val repository = dao?.let { AttendanceRepository(it) }
        val factory = repository?.let { AttendanceViewModelFactory(it) }
        viewModel =
            factory?.let { ViewModelProvider(this, it)[AttendanceViewModel::class.java] }!!

        val args: DatesFragmentArgs by navArgs()

        attendance = args.selectedItem
        position = args.positionItem
        binding.tvSubjectName.text = attendance.subject
        present = attendance.present
        absent = attendance.absent
        total = attendance.total
        percentage = attendance.percentage
        noClassRequired(present, total, percentage)

        binding.btnAddDate.setOnClickListener {

            viewModel.viewModelScope.launch {
                viewModel.getSubjectAttendance(attendance.id)
                    .observe(viewLifecycleOwner) {
                        present = it.present
                        absent = it.absent
                        total = present + absent

                        if (total != 0)
                            percentage = (present * 100) / total

                        noClassRequired(present, total, percentage)

                        if (binding.btnSaveDate.visibility == View.GONE)
                            itemTouchHelperCallback.apply {
                                swipeEnabled = true
                                binding.rvDates.apply {
                                    layoutManager.apply {
                                        canScroll = true
                                    }
                                }
                            }
                        else
                            itemTouchHelperCallback.apply {
                                swipeEnabled = false
                                binding.rvDates.apply {
                                    layoutManager.apply {
                                        canScroll = false
                                    }
                                }
                            }
                    }
                attendance.total = present + absent
                attendance.present = present
                attendance.absent = absent
                attendance.percentage = percentage
                pickDate()
            }
        }
        itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position1 = viewHolder.adapterPosition
                val attendanceData = rvAdapter.differ.currentList[position1]
                viewModel.deleteAttendanceData(attendanceData,
                    rvAdapter.differ.currentList,
                    attendance)
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                if (swipeEnabled)
                    return true
                return false
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvDates)
        }
        initRv()
    }

    private fun noClassRequired(present: Int, total: Int, percentage: Int) {
        if (percentage < 75) {
            val classesRequired = (((0.75 * total) - present) / 0.25).toInt()
            if (classesRequired != 0)
                binding.tvStatus.text =
                    "You have to attend $classesRequired more classes to maintain 75% attendance"
        } else {
            binding.tvStatus.text = "Your attendance is above 75%. Keep up!!"
        }
    }

    private fun pickDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
        datePicker.addOnPositiveButtonClickListener {
            val date = datePicker.headerText
            updateAttendance(date)

        }
        datePicker.show(parentFragmentManager, "Mytag")
    }

    private fun initRv() {
        binding.rvDates.apply {
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    if (canScroll)
                        return true
                    return false
                }
            }

        }

        rvAdapter = AttendanceDataRecyclerViewAdapter(binding.tvStatus, binding.btnAddDate,
            attendance,
            binding.btnSaveDate,
            viewModel)
        binding.rvDates.adapter = rvAdapter
        displaySubjectAttendanceList()
    }

    private fun updateAttendance(date: String) {
        val list = rvAdapter.differ.currentList.toMutableList()
        val attendanceData = AttendanceData("", "", date)
        list.add(attendanceData)
        viewModel.updateAttendanceData(Attendance(attendance.id,
            attendance.subject,
            ArrayList(list),
            attendance.total, attendance.present, attendance.absent,
            attendance.percentage))

        binding.btnAddDate.visibility = View.GONE
        binding.btnSaveDate.visibility = View.VISIBLE
    }

    private fun displaySubjectAttendanceList() {
        viewModel.getAttendanceData(attendance, position).observe(viewLifecycleOwner) {
            rvAdapter.differ.submitList(it)
            if (rvAdapter.differ.currentList.size != 0)
                binding.rvDates.smoothScrollToPosition(rvAdapter.differ.currentList.size)

        }
    }

    override fun onStop() {
        if (binding.btnSaveDate.visibility == View.VISIBLE) {
            val attendanceList = ArrayList(rvAdapter.differ.currentList)

            if (attendanceList.size != 0)
                attendanceList.remove(attendanceList[attendanceList.size - 1])
            else
                attendanceList.remove(attendanceList[attendanceList.size])

            viewModel.updateAttendanceData(Attendance(attendance.id,
                attendance.subject,
                attendanceList,
                attendance.total, attendance.present, attendance.absent,
                attendance.percentage))
        }
        super.onStop()
    }
}