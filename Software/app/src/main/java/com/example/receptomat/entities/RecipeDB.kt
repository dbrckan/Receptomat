package com.example.receptomat.entities

import android.os.Parcel
import android.os.Parcelable

data class RecipeDB(
    val recipe_id: Int?,
    val name: String,
    val description: String,
    val time: Int,
    val user_id: Int,
    val category_id: Int,
    val preference_id: Int,
    val image_path: String? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(time)
        parcel.writeInt(user_id)
        parcel.writeInt(category_id)
        parcel.writeInt(preference_id)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RecipeDB> {
        override fun createFromParcel(parcel: Parcel): RecipeDB {
            return RecipeDB(parcel)
        }

        override fun newArray(size: Int): Array<RecipeDB?> {
            return arrayOfNulls(size)
        }
    }
}
