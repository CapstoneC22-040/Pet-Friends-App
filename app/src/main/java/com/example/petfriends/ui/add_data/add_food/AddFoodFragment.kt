package com.example.petfriends.ui.add_data.add_food

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.petfriends.R
import com.example.petfriends.data.local.model.PetFood
import com.example.petfriends.databinding.FragmentAddFoodBinding
import com.example.petfriends.ui.MainActivity
import com.example.petfriends.utils.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AddFoodFragment : Fragment() {
    private var _binding: FragmentAddFoodBinding? = null
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
        _binding = FragmentAddFoodBinding.inflate(inflater, container, false)
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
                    setPositiveButton(getString(R.string.yes)){_, _ ->
                        showEditHours(true)
                        ivChangeHours.visibility = View.GONE
                        saveHours()
                        show().dismiss()
                    }
                    setNegativeButton(getString(R.string.no)){_, _ ->
                        show().dismiss()
                    }
                    create()
                    show()
                }
            }
            ivChangeDate.setOnClickListener { changeDay() }
        }


        addFood()
    }

    private fun saveHours() {
        binding.apply {
            ivSaveHours.visibility = View.VISIBLE
            ivSaveHours.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.save_hours))
                    setPositiveButton(getString(R.string.yes)){_, _ ->
                        showEditHours(false)
                        hours = edHours.text.toString()
                        tvHours.text = hours
                        ivSaveHours.visibility = View.GONE
                        show().dismiss()
                    }
                    setNegativeButton(getString(R.string.no)){_, _ ->
                        show().dismiss()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun addFood() {
        showLoading(false)
        binding.apply {

            tvHours.text = DateHelper.getCurrentHours()
            tvDay.text = DateHelper.getCurrentDay()
            tvDate.text = DateHelper.getCurrentDate()
            btnFoodNext.setOnClickListener {
                val hours = tvHours.text.toString()
                val day = tvDay.text.toString()
                val date = tvDate.text.toString()
                val cattegoryName = "Food"
                val foodName = edFoodName.text.toString()
                val foodWeight = edFoodWeight.text.toString()
                val uId = mAuth.currentUser?.uid.toString()
                val createdAt = DateHelper.getCurrentTimeStamp()
                when {
                    foodName.isEmpty() -> {
                        edFoodName.error = getString(R.string.error_food_name)
                    }
                    foodWeight.isEmpty() -> {
                        edFoodWeight.error = getString(R.string.error_weight)
                    }
                    else -> {

                        database = FirebaseDatabase.getInstance().getReference("PetsFoods")
                        val petFoodId = database.push().key.toString()
                        val petFood = PetFood(
                            uId,
                            petFoodId,
                            cattegoryName,
                            foodName,
                            foodWeight,
                            hours,
                            day,
                            date,
                            createdAt
                        )
                        database.child(uId).child(petFoodId).setValue(petFood).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showLoading(false)
                                Log.w(TAG, "success")
                                AlertDialog.Builder(context).apply{
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.success_add_food))
                                    setPositiveButton(getString(R.string.cont)){_, _ ->
                                        show().dismiss()
                                        edFoodName.setText("")
                                        edFoodWeight.setText("")
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
                                    setMessage(getString(R.string.success_add_food))
                                    setPositiveButton(getString(R.string.cont)){_, _ ->
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
        binding.pbAddpetfood.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        private const val TAG = "AddFoodFragment"
    }

}