package _homeScreen.DataBase

import java.io.Serializable


data class PartnerProfile(
    val nome: String = "",
    val idade: Int = 0,
    val resumo: String = "",
    val picture: String = ""
) : Serializable {

    constructor() : this("", 0, "", "")
}