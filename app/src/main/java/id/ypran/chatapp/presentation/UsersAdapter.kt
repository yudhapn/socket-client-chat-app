package id.ypran.chatapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.chatapp.data.User
import id.ypran.chatapp.data.UserDiffCallback
import id.ypran.chatapp.databinding.ItemUserLayoutBinding
import id.ypran.chatapp.presentation.UsersAdapter.ViewHolder

class UsersAdapter(private val listener: UsersAdapterListener) :
    ListAdapter<User, ViewHolder>(UserDiffCallback) {
    interface UsersAdapterListener {
        fun onUserClicked(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(listener, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemUserLayoutBinding,
        private val listener: UsersAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.user = user
            binding.listener = listener
        }

        companion object {
            fun from(listener: UsersAdapterListener, parent: ViewGroup) = ViewHolder(
                ItemUserLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                listener
            )
        }
    }
}