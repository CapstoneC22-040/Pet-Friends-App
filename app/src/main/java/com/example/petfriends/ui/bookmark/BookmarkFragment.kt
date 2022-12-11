package com.example.petfriends.ui.bookmark

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.petfriends.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriends.data.local.model.ItemList
import com.example.petfriends.data.local.model.PetFood
import com.example.petfriends.data.local.model.PetFoodUpdate
import com.example.petfriends.databinding.FragmentBookmarkBinding
import com.example.petfriends.ui.adapter.ListItemAdapter
import com.example.petfriends.utils.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

class BookmarkFragment : Fragment() {

    private var _binding : FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var uDatabase : DatabaseReference
    private lateinit var dDatabase : DatabaseReference

    private lateinit var itemList: ArrayList<ItemList>
    private lateinit var listItemAdapter: ListItemAdapter

    private var key: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        CalendarUtils.selectedDate = LocalDate.now()

        uDatabase = Firebase.database.reference

        itemList = arrayListOf<ItemList>()

        mAuth = Firebase.auth

        actionRecylerList()

        listItemFoods()

    }

    private fun actionRecylerList() {
        listItemAdapter = ListItemAdapter(itemList)
        binding.rvListFoodItem.apply {
            adapter = listItemAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        listItemAdapter.setOnItemClickCallback(object : ListItemAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ItemList) {
                showUpdateDialog(data)
            }

        })


    }

    private fun showUpdateDialog(data: ItemList) {
        val dialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val viewInflater = inflater.inflate(R.layout.update_food_dialog, null)

        dialog.setView(viewInflater)
        dialog.setTitle(getString(R.string.update))

        val user = mAuth.currentUser


        val edName = viewInflater.findViewById<EditText>(R.id.update_ed_food_name)
        val edWeight = viewInflater.findViewById<EditText>(R.id.update_ed_food_weight)
        val tvDate = viewInflater.findViewById<TextView>(R.id.tv_date)

        edName.setText(data.foodName)
        edWeight.setText(data.foodWeight)
        tvDate.setText(data.date)

        dialog.setPositiveButton(getString(R.string.update)){_, _ ->
            val foodName = edName.text.toString()
            val foodWeight = edWeight.text.toString()
            val date = tvDate.text.toString()
            when {
                foodName.isEmpty() -> {
                    edName.error = getString(R.string.error_food_name)

                }
                foodWeight.isEmpty() -> {
                    edWeight.error = getString(R.string.error_weight)
                }
                else -> {
                    val key = data.petFoodId
                    val uId = mAuth.currentUser?.uid.toString()

                    val petFoodUpdate = PetFood(
                        uId,
                        key,
                        categoryName = "Food",
                        foodName,
                        foodWeight,
                        hours = DateHelper.getCurrentHours(),
                        day = DateHelper.getCurrentDay(),
                        date,
                        createdAt = DateHelper.getCurrentTimeStamp()
                    )

                    val petFoodValue = petFoodUpdate.toMap()

                    val childUpdate = hashMapOf<String, Any>(
                        "/PetsFoods/$uId/$key" to petFoodValue
                    )

                    uDatabase.updateChildren(childUpdate).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Success")
                            Toast.makeText(context, "Success update data", Toast.LENGTH_SHORT).show()
                            listItemAdapter.notifyDataSetChanged()
                        }else {
                            Log.d(TAG, "Failed")
                        }
                    }
                }
            }
        }
        dialog.setNegativeButton(getString(R.string.delete)){_, _ ->
            dDatabase = FirebaseDatabase.getInstance()
                .getReference("PetsFoods").child(user?.uid.toString()).child(data.petFoodId.toString())
            dDatabase.removeValue()
            Toast.makeText(context, "Success delete", Toast.LENGTH_SHORT).show()
            listItemAdapter.notifyDataSetChanged()
        }

        dialog.setNeutralButton(getString(R.string.no)){_, _ ->

        }

        val alert = dialog.create()
        alert.show()
    }

    private fun listItemFoods() {
        val user = mAuth.currentUser
        database = FirebaseDatabase.getInstance().getReference("PetsFoods")
        database.child(user?.uid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    key = snapshot.key.toString()
                    database.child(key!!).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (itemSnapshot in snapshot.children) {
                                    val item = itemSnapshot.getValue(ItemList::class.java)
                                    itemList.add(item!!)
                                    listItemAdapter.notifyDataSetChanged()
                                }
                                Log.d(TAG, "Success")
                            }
                            else {
                                Log.d(TAG, "Failed")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, error.message)
                        }
                    })
                }
                else {
                    Log.d(TAG, "Failed")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "BookmarkFragment"
    }
}