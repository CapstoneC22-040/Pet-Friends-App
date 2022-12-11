package com.example.petfriends.ui.category

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.example.petfriends.R
import com.example.petfriends.data.local.model.ItemList
import com.example.petfriends.databinding.FragmentCategoryBinding
import com.example.petfriends.ui.adapter.ListItemAdapter
import com.example.petfriends.ui.add_data.add_pet.FirstAddPetFragment
import com.example.petfriends.ui.bookmark.BookmarkFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class CategoryFragment : Fragment() {


    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private lateinit var catImage:Drawable
    private lateinit var dogImage:Drawable
    private lateinit var jenisPet: String

    private var catTextColor:Int? = null
    private var dogTextColor:Int? = null

    private var _binding : FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            binding.btnAddFood.setOnClickListener (
                Navigation.createNavigateOnClickListener(R.id.action_navigation_category_to_addFoodFragment)
            )

            binding.btnAddShower.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_navigation_category_to_addShowerFragment)
            )

        }


        mAuth = Firebase.auth

        catImage =  ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.cat_image,null) as Drawable
        dogImage = ResourcesCompat.getDrawable(requireActivity().resources,R.drawable.dog_image,null) as Drawable
        catTextColor = ResourcesCompat.getColor(requireActivity().resources,R.color.pink,null)// Pink
        dogTextColor = ResourcesCompat.getColor(requireActivity().resources,R.color.blue,null)

        setupItem()
    }

    private fun setupItem() {
        val user = mAuth.currentUser
        database = FirebaseDatabase.getInstance().getReference("Pets")
        database.child(user?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("petName").value.toString()
                    val gender = snapshot.child("petGender").value.toString()
                    val typePet = snapshot.child("petJenis").value.toString()
                    val petBirthday = snapshot.child("petBirthday").value.toString()

                    binding.petName.text = getString(R.string.pet_name_text, name)
                    binding.petType.text = getString(R.string.pet_jenis_text,typePet)
                    binding.petGender.text = getString(R.string.pet_gender_text,gender)
                    binding.petBirthday.text = getString(R.string.pet_birthday_text, petBirthday)

                    Log.e("TAG","$name")
                    Toast.makeText(requireActivity(),"$name",Toast.LENGTH_SHORT).show()

                    if(typePet == "Cat"){
                        binding.apply {
                            ivPet.setImageDrawable(catImage)
                            cvInformation.strokeColor = catTextColor!!
                            petName.setTextColor(catTextColor!!)
                        }
                    } else {
                        binding.apply {
                            ivPet.setImageDrawable(dogImage)
                            cvInformation.strokeColor = dogTextColor!!
                            petName.setTextColor(dogTextColor!!)
                        }
                    }

             }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CategoryFragment"
    }

}