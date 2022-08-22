package com.example.myattendancetracker.ui.attendance

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendancetracker.R
import com.example.myattendancetracker.databinding.FragmentAttendanceBinding
import com.example.myattendancetracker.db.Attendance
import com.example.myattendancetracker.db.AttendanceData
import com.example.myattendancetracker.db.AttendanceDatabase
import com.example.myattendancetracker.db.AttendanceRepository
import com.example.myattendancetracker.ui.adapters.AttendanceRecyclerViewAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AttendanceFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var rvAdapter: AttendanceRecyclerViewAdapter
    private lateinit var attendanceViewModel: AttendanceViewModel
    private val list = ArrayList<AttendanceData>()
    private lateinit var subject: String

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dao = context?.let {
            AttendanceDatabase.getInstance(requireContext()).attendanceDao()
        }
        val repository = dao?.let { AttendanceRepository(it) }
        val factory = repository?.let { AttendanceViewModelFactory(it) }
        attendanceViewModel =
            factory?.let { ViewModelProvider(this, it)[AttendanceViewModel::class.java] }!!
        binding = FragmentAttendanceBinding.bind(view)



        initRv()
        binding.floatingActionButton.setOnClickListener {
            showdialog()
        }
        val itemTouchHelperCallback = object:
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val attendance = rvAdapter.differ.currentList[position]
                attendanceViewModel.deleteSubjectAttendance(attendance)
                Snackbar.make(view!!,"Deleted Successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        attendanceViewModel.insertSubject(attendance)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSubjects)
        }
        rvAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("selected_item", it)
                putSerializable("position_item",rvAdapter.differ.currentList.indexOf(it))
            }

            findNavController().navigate(R.id.action_nav_attendance_to_nav_date, bundle)        }

    }

    private fun initRv() {
        binding.rvSubjects.layoutManager = LinearLayoutManager(context)
        rvAdapter = AttendanceRecyclerViewAdapter()
        binding.rvSubjects.adapter = rvAdapter
        displaySubjectAttendanceList()
    }
    private fun displaySubjectAttendanceList() {

        attendanceViewModel.getALlSubjectAttendance().observe(viewLifecycleOwner) {
            rvAdapter.differ.submitList(it)
        }
    }


    private fun showdialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Title")

// Set up the input
        val input = EditText(context)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            // Here you get get input text from the Edittext
            subject = input.text.toString()
            attendanceViewModel.viewModelScope.launch {
                attendanceViewModel.insertSubject(Attendance(0, subject, list, 0, 0, 0, 0))
            }

        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }
}