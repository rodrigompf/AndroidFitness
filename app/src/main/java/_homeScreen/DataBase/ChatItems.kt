package _homeScreen.DataBase

data class ChatItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val unreadCount: Int,
    val partnerId: String  // Add partnerId field to store partner's ID
)
