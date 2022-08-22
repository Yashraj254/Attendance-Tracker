package com.example.myattendancetracker.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myattendancetracker.R
import com.example.myattendancetracker.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var myMenu: Menu
    private lateinit var sp: SharedPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
         sp = activity!!.getPreferences(Context.MODE_PRIVATE)
        readData()
        profileViewModel.text.observe(viewLifecycleOwner) {
            //  textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        myMenu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save ->{
                writeData()
                readData()
                changeVisibilityToEdit()
            }
            R.id.action_edit -> {
                changeVisibilityToSave()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeVisibilityToEdit() {

        readData()
        binding.etField.visibility = View.GONE
        binding.tvField.visibility = View.VISIBLE
        myMenu.findItem(R.id.action_edit).isVisible = true
        myMenu.findItem(R.id.action_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        myMenu.findItem(R.id.action_save).isVisible = false
        myMenu.findItem(R.id.action_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
    }

    private fun changeVisibilityToSave() {
        writeData()
        binding.etField.visibility = View.VISIBLE
        binding.tvField.visibility = View.GONE
        myMenu.findItem(R.id.action_edit).isVisible = false
        myMenu.findItem(R.id.action_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        myMenu.findItem(R.id.action_save).isVisible = true
        myMenu.findItem(R.id.action_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }
    private fun readData(){
        binding.tvName.text =   "Name:      " + sp.getString("sp_name","")
        binding.tvClass.text =  "Class:     " + sp.getString("sp_class","")
        binding.tvRollNo.text = "Enroll No. " + sp.getString("sp_enroll","")
        binding.tvCollege.text ="College:   " + sp.getString("sp_college","")
    }
    private fun writeData(){
        sp.edit {
            putString("sp_name",binding.etName.text.toString())
            putString("sp_class",binding.etClass.text.toString())
            putString("sp_enroll",binding.etEnroll.text.toString())
            putString("sp_college",binding.etCollege.text.toString())
            apply()
        }
    }
}