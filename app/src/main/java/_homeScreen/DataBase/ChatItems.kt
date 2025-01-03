package _homeScreen.DataBase

import android.graphics.Bitmap

data class ChatItem(
    val id: String,
    val name: String,
    val imageBitmap: Bitmap? = null, // For Base64 decoded images
    val imageUrl: String? = null,   // For direct URLs
    val unreadCount: Int,
    val partnerId: String
)

