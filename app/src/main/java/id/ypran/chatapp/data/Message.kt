package id.ypran.chatapp.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: Int,
    val text: String,
    val user: User
) : Parcelable

object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem == newItem

}
