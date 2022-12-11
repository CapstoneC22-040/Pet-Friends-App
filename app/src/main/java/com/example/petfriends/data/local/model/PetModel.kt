package com.example.petfriends.data.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import java.util.*

//data class PetModel(
//    var typePet:String? = null
//)

@Parcelize
data class PetModel(
    val uId: String,
    val petPhotoUrl: String,
    val petName: String,
//    val petAge: Int,
    val petJenis: String,
    val petGender: String,
    val petBirthday: String,
    val createdAt: String,
) : Parcelable


data class PetFood(
    var uId: String? = null,
    var petFoodId: String? = null,
    var categoryName: String? = null,
    var foodName: String? = null,
    var foodWeight: String? = null,
    val hours: String? = null,
    val day: String? = null,
    val date: String? = null,
    var createdAt: String? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
//            "petFoodId" to petFoodId,
//            "categoryName" to categoryName,
            "foodName" to foodName,
            "foodWeight" to foodWeight,
//            "hours" to hours,
//            "day" to day,
//            "date" to hours,
//            "createdAt" to createdAt
        )
    }
}

data class PetFoodUpdate(
    var foodName: String? = null,
    var foodWeight: String? = null,
    val date: String? = null,
)




data class PetMedicine(
    val petMedicineId: String,
    val petId: String,
    val uId: String,
    val petMedicineName: String,
    val petMedicineKind: String,
    val petMedicineDate: Date?,
    val createdAt: Date?
)


data class ItemList(
    val petFoodId: String? = null,
    val date: String? = null,
    val categoryName: String? = null,
    val foodName: String? = null,
    val uId: String? = null,
    val foodWeight: String? = null
)