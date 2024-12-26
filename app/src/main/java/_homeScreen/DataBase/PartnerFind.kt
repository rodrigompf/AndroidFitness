package _homeScreen.DataBase

import android.os.Parcel
import android.os.Parcelable

data class PartnerProfile(
    val nome: String,
    val idade: Int,
    val resumo: String,
    val picture: String,
    val images: List<String> // Added the images field
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList() // Read images list from parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeInt(idade)
        parcel.writeString(resumo)
        parcel.writeString(picture)
        parcel.writeStringList(images) // Write images list to parcel
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PartnerProfile> {
        override fun createFromParcel(parcel: Parcel): PartnerProfile = PartnerProfile(parcel)
        override fun newArray(size: Int): Array<PartnerProfile?> = arrayOfNulls(size)
    }
}