package com.example.petfriends.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

@Entity
data class Pet(
    @PrimaryKey
    val petId: String,
    val uId: String,
    val petPhotoUrl: String,
    val petName: String,
    val petAge: Int,
    val petJenis: String,
    val petGender: String,
    val petBirthday: String,
    val createdAt: String
)

@Entity
data class PetFood(
    @PrimaryKey
    var petFoodId: String,
    var petId: String? = null,
    var uId: String? = null,
    var petFoodName: String? = null,
    var petFoodKind: String? = null,
    var petFoodWeight: String?= null,
    var petFoodDate: String? = null,
    val hours: String? = null,
    val day: String? = null,
    val date: String? = null,
    var createdAt: String? = null
)

@Entity
data class PetMedicine(
    @PrimaryKey
    val petMedicineId: String,
    val petId: String,
    val uId: String,
    val petMedicineName: String,
    val petMedicineKind: String,
    val petMedicineDate: Date?,
//    val createdAt: Date?
)

@Entity
data class PetVaccine(
    @PrimaryKey
    val petVaccineId: String,
    val petId: String,
    val uId: String,
    val petVaccineName: String,
    val petVaccineKind: String,
    val petVaccineDate: Date?,
//    val createdAt: Date?
)

@Entity
data class PetShower(
    @PrimaryKey
    val petShowerId: String,
    val uId: String,
    val manageShower: String,
    val waterType: String,
    val hours: String,
    val day: String,
    val date: String,
    val createdAt: String,
)

data class UserAndPet(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "uId",
        entityColumn = "uId"
    )
    val pet: Pet
)

data class PetAndFoods(
    @Embedded val pet: Pet,
    @Relation(
        parentColumn = "petId",
        entityColumn = "petId"
    )
    val petFood: List<PetFood>
)

data class PetAndMedicine(
    @Embedded val pet: Pet,
    @Relation(
        parentColumn = "petId",
        entityColumn = "petId"
    )
    val petMedicine: List<PetMedicine>
)

data class PetAndVaccine(
    @Embedded val pet: Pet,
    @Relation(
        parentColumn = "petId",
        entityColumn = "petId"
    )
    val petVaccine: List<PetVaccine>
)

data class PetAndShower(
    @Embedded val pet: Pet,
    @Relation(
        parentColumn = "petId",
        entityColumn = "petId"
    )
    val petShower: PetShower
)
