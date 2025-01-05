package _homeScreen.DataBase

data class MessageItem(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val partnerProfileImage: String? = null // Make sure this field is populated correctly
)


