package com.example.receptomat.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Recipe(
    val recipe_id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val preparationTime: Int,
    val image_path: String? = null,
    val dateOfAddition: Date = Date()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readSerializable() as Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(recipe_id)
        parcel.writeString(name)
        parcel.writeStringList(ingredients)
        parcel.writeString(instructions)
        parcel.writeInt(preparationTime)
        parcel.writeString(image_path)
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