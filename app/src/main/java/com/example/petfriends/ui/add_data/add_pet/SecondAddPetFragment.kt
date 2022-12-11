package com.example.petfriends.ui.add_data.add_pet

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.example.petfriends.R
import com.example.petfriends.data.local.model.PetModel
import com.example.petfriends.databinding.FragmentSecondAddPetBinding
import com.example.petfriends.ui.MainActivity
import com.example.petfriends.utils.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*

class SecondAddPetFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentSecondAddPetBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var jenisPet: String

    private lateinit var catImage:Drawable
    private lateinit var dogImage:Drawable
    private var catTextColor:Int? = null
    private var dogTextColor:Int? = null

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondAddPetBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Auth from firebase
        mAuth = Firebase.auth

        //Set Style based on pet type
        catImage =  ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.cat_image,null) as Drawable
        dogImage = ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.dog_image,null) as Drawable
        catTextColor =
            ResourcesCompat.getColor(requireActivity().resources,R.color.pink,null)// Pink
        dogTextColor = ResourcesCompat.getColor(requireActivity().resources,R.color.blue,null)

        //get Jenis from first add
        jenisPet = arguments?.getString(FirstAddPetFragment.TYPE_NAME).toString()


        if (jenisPet == "Cat"){
            binding.apply {
                ivTypePet.setImageDrawable(catImage)
                tvJenisPet.setTextColor(catTextColor!!)
                cvInputData.strokeColor = catTextColor!!
            }


        } else{
            binding.apply {
                ivTypePet.setImageDrawable(dogImage)
                tvJenisPet.setTextColor(dogTextColor!!)
                cvInputData.strokeColor = dogTextColor!!
            }

        }

        binding.buttonCalendar.setOnClickListener {

            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(requireContext(),this , year, month,day)
            datePickerDialog.show()

        }

        binding.tvJenisPet.text = jenisPet
        setAddAction()
    }

    private fun setAddAction() {
        binding.apply {
            binding.btnAddPet.setOnClickListener {
//                val petId = "01"
                val petName = edNamePet.text.toString()
                val petPhoto = edPhotoPet.text.toString()
                val date = tvBirthday.text.toString()
                val gender = edGenderPet.text.toString()
                val uId = mAuth.currentUser?.uid.toString()
                val createdAt = DateHelper.getCurrentDate()

                when {
                    petName.isEmpty() -> {
                        edNamePet.error = "Error"
                    }
                    petPhoto.isEmpty() -> {
                        edPhotoPet.error = "Error"
                    }
                    gender.isEmpty() -> {
                        edGenderPet.error = "Error"
                    }
                    else -> {
                        val petModel = PetModel(
                            uId,
                            petPhoto,
                            petName,
                            jenisPet,
                            gender,
                            date,
                            createdAt,
                        )

                        database = FirebaseDatabase.getInstance().getReference("Pets")
                        database.child(uId).setValue(petModel).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.w(TAG, "success")
                                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            else {
                                Log.w(TAG, "failure", task.exception)
                                Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "SecondAddPetFragment"
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month+1
        binding.tvBirthday.text = "$myYear/$myMonth/$myDay"
    }

}