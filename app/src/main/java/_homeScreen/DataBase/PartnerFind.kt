package _homeScreen.DataBase

import java.io.Serializable

data class PartnerProfile(
    val nome: String = "",
    val idade: Int = 0,
    val resumo: String = "",
    val picture: String = "",
    val images: List<String> = listOf()  // List to store all partner photos
) : Serializable {

    constructor() : this("", 0, "", "", listOf())
}
