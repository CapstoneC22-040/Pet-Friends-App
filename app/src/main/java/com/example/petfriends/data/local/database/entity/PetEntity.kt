package com.example.petfriends.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.text.DateFormat

@Entity
data class Pet(
    @PrimaryKey
    val petId: String,
    val uId: String,
    val petPhotoUrl: String,
    val petName: String,
    val petAge: Int,
    val petDate: DateFormat,
    val petJenis: String,
    val petGender: String,
    val createdAt: DateFormat
)

@Entity
data class PetFood(
    @PrimaryKey
    val petFoodId: String,
    val petId: String,
    val uId: String,
    val petFoodName: String,
    val petFoodKind: String,
    val petFoodWeight: Long,
    val createdAt: DateFormat
)

@Entity
data class PetMedicine(
    @PrimaryKey
    val petMedicineId: String,
    val petId: String,
    val uId: String,
    val petMedicineName: String,
    val petMedicineKind: String,
    val petMedicineDate: DateFormat,
    val createdAt: DateFormat
)

@Entity
data class PetVaccine(
    @PrimaryKey
    val petVaccineId: String,
    val petId: String,
    val uId: String,
    val petVaccineName: String,
    val petVaccineKind: String,
    val petVaccineDate: DateFormat,
    val createdAt: DateFormat
)

@Entity
data class PetShower(
    @PrimaryKey
    val petShowerId: String,
    val petId: String,
    val uId: String,
    val petShampooName: String,
    val petShowerDate: DateFormat,
    val createdAt: DateFormat
)

data class UserAndPet(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "uId",
        entityColumn = "uId"
    )
    val pet: Pet
)

data class PetAndFood(
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