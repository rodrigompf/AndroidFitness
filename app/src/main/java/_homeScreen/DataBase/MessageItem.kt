package _homeScreen.DataBase

data class MessageItem(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val partnerProfileImage: String? = null // Add this field for the partner's profile image (Base64 or URL)
) {
    constructor(senderId: String, message: String, timestamp: Any?, partnerProfileImage: String?) : this(
        senderId,
        message,
        when (timestamp) {
            is Long -> timestamp
            is String -> timestamp.toLongOrNull() ?: 0L
            else -> 0L
        },
        partnerProfileImage
    )
}

