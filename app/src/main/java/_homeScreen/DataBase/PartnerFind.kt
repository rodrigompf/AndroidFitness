package _homeScreen.DataBase

import android.os.Parcel
import android.os.Parcelable

data class PartnerProfile(
    val id: String = "",
    val nome: String = "",
    val idade: Int = 0,
    val resumo: String = "",
    val picture: String = "",
    val images: List<String> = listOf(),
    val vote: Boolean? = null // Add vote field, default to null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean // Handle the vote field
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nome)
        parcel.writeInt(idade)
        parcel.writeString(resumo)
        parcel.writeString(picture)
        parcel.writeStringList(images)
        parcel.writeValue(vote) // Write vote field to parcel
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PartnerProfile> {
        override fun createFromParcel(parcel: Parcel): PartnerProfile = PartnerProfile(parcel)
        override fun newArray(size: Int): Array<PartnerProfile?> = arrayOfNulls(size)
    }
}

