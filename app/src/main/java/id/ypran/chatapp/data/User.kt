package id.ypran.chatapp.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String
) : Parcelable

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem

}