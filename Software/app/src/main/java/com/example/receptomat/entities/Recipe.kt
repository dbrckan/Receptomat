package com.example.receptomat.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Recipe(
    val recipe_id: Int?,
    val name: String,
    val meal: Meal,
    val description: String,
    val time: Int,
    val user_id: Int,
    val category_id: Int,
    val preference_id: Int,
    val ingredients: List<String>,
    val instructions: String,
    val preparationTime: Int,
    val image_path: String? = null,
    val dateOfAddition: Date = Date()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readSerializable() as Meal,
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readSerializable() as Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeSerializable(meal)
        parcel.writeStringList(ingredients)
        parcel.writeString(instructions)
        parcel.writeInt(preparationTime)
        parcel.writeString(image_path)
        parcel.writeSerializable(dateOfAddition)
        parcel.writeString(description)
        parcel.writeInt(time)
        parcel.writeInt(user_id)
        parcel.writeInt(category_id)
        parcel.writeInt(preference_id)
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
