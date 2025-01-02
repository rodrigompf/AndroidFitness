package _homeScreen.DataBase

data class MessageItem(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
) {
    constructor(senderId: String, message: String, timestamp: Any?) : this(
        senderId,
        message,
        when (timestamp) {
            is Long -> timestamp
            is String -> timestamp.toLongOrNull() ?: 0L
            else -> 0L
        }
    )
}
