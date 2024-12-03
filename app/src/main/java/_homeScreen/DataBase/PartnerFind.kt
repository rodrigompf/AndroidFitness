package _homeScreen.DataBase

data class PartnerProfile(
    val nome: String = "",
    val idade: Int = 0,
    val descricao: String = "",  // Renamed 'descrição' to 'descricao' (for valid identifier in Kotlin)
    val picture: String = ""    // Renamed 'Picture' to 'picture' (to maintain consistency)
)