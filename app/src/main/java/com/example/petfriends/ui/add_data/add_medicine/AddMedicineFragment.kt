package com.example.petfriends.ui.add_data.add_medicine

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.petfriends.R
import com.example.petfriends.data.local.model.PetMedicineModel
import com.example.petfriends.data.local.model.PetModel
import com.example.petfriends.databinding.FragmentAddMedicineBinding
import com.example.petfriends.utils.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class AddMedicineFragment : Fragment() {
    private var _binding: FragmentAddMedicineBinding? = null
    private val binding get() = _binding!!


    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var hours: String
    private lateinit var day: String
    private lateinit var date: String

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicineBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(true)

        mAuth = Firebase.auth

        hours = String()


        binding.apply {
            ivChangeHours.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.edit_hours))
                    setPositiveButton(getString(R.string.yes)){ _, _ ->
                        showEditHours(true)
                        ivChangeHours.visibility = View.GONE
                        saveHours()
                        show().dismiss()
                    }
                    setNegativeButton(getString(R.string.no)){ _, _ ->
                        show().dismiss()
                    }
                    create()
                    show()
                }
            }
            ivChangeDate.setOnClickListener { changeDay() }
        }


        addMedicine()
    }

    private fun saveHours() {
        binding.apply {
            ivSaveHours.visibility = View.VISIBLE
            ivSaveHours.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.save_hours))
                    setPositiveButton(getString(R.string.yes)){ _, _ ->
                        showEditHours(false)
                        hours = edHours.text.toString()
                        tvHours.text = hours
                        ivSaveHours.visibility = View.GONE
                        show().dismiss()
                    }
                    setNegativeButton(getString(R.string.no)){ _, _ ->
                        show().dismiss()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun addMedicine() {
        showLoading(false)
        binding.apply {

            tvHours.text = DateHelper.getCurrentHours()
            tvDay.text = DateHelper.getCurrentDay()
            tvDate.text = DateHelper.getCurrentDate()
            btnMedicineNext.setOnClickListener {
                val hours = tvHours.text.toString()
                val day = tvDay.text.toString()
                val date = tvDate.text.toString()
                val urlPhoto = "asdasdasd"
                val medicineName = edMedicineName.text.toString()
                val medicineWeight = edMedicineWeight.text.toString()
                val uId = mAuth.currentUser?.uid.toString()
                val createdAt = DateHelper.getCurrentDate()
                when {
                    medicineName.isEmpty() -> {
                        edMedicineName.error = getString(R.string.error_medicine_name)
                    }
                    medicineWeight.isEmpty() -> {
                        edMedicineWeight.error = getString(R.string.error_weight)
                    }
                    else -> {
                        val petMedicine = PetMedicineModel(
                            uId,
                            urlPhoto,
                            medicineName,
                            medicineWeight,
                            hours,
                            day,
                            date,
                            createdAt
                        )
                        database = FirebaseDatabase.getInstance().getReference("PetsMedicines")
                        database.child(uId).push().setValue(petMedicine).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showLoading(false)
                                Log.w(TAG, "success")
                                AlertDialog.Builder(context).apply{
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.success_add_medicine))
                                    setPositiveButton(getString(R.string.cont)){ _, _ ->
                                        show().dismiss()
                                    }
                                    create()
                                    show()
                                }
                            }
                            else {
                                showLoading(false)
                                Log.w(TAG, "failure", task.exception)
                                AlertDialog.Builder(context).apply{
                                    setTitle(getString(R.string.failed))
                                    setMessage(getString(R.string.success_add_medicine))
                                    setPositiveButton(getString(R.string.cont)){ _, _ ->
                                        show().dismiss()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeDay() {
        binding.apply {
            tvDate.visibility = View.GONE
            dpDay.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.pbAddpetmedicine.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showEditHours(isEdit: Boolean) {
        binding.apply {
            if (isEdit) {
                tvHours.visibility = View.GONE
                edHours.visibility = View.VISIBLE
            } else {
                tvHours.visibility = View.VISIBLE
                edHours.visibility = View.GONE
            }
        }
    }

    private fun showEditDay(isEdit: Boolean) {
        binding.apply {
            if (isEdit) {
                tvDay.visibility = View.GONE
                tvDate.visibility = View.GONE
                dpDay.visibility = View.VISIBLE
                edDate.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AddMedicineFragment"
    }

}