package id.ypran.chatapp.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: Int,
    val users: List<User>,
    val messages: List<Message>,
    val lastMessage: Message
) : Parcelable

object ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem == newItem

}