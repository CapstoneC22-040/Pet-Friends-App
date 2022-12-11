package com.example.petfriends.ui.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.petfriends.data.local.database.repository.PetRepository
import com.example.petfriends.ui.add_data.add_food.viewmodel.AddPetFoodViewModel
import com.example.petfriends.ui.add_data.add_pet.viewmodel.AddPetViewModel
import java.util.*

class ViewModelFactory private constructor(
    private val petRepository: PetRepository
    ) : ViewModelProvider.Factory {


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    PetRepository.getInstance(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(AddPetFoodViewModel::class.java) -> {
                AddPetFoodViewModel(petRepository) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }



}