package com.example.receptomat.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Recipe(
    val id: Int,
    val name: String,
    val meal: Meal,
    val ingredients: List<String>,
    val instructions: String,
    val preparationTime: Int,
    val picture: String? = null,
    val dateOfAddition: Date = Date()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readSerializable() as Meal,
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readSerializable() as Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeSerializable(meal)
        parcel.writeStringList(ingredients)
        parcel.writeString(instructions)
        parcel.writeInt(preparationTime)
        parcel.writeString(picture)
        parcel.writeSerializable(dateOfAddition)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
