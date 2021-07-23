package id.ypran.chatapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.chatapp.data.Chat
import id.ypran.chatapp.data.ChatDiffCallback
import id.ypran.chatapp.data.User
import id.ypran.chatapp.databinding.ItemChatLayoutBinding
import id.ypran.chatapp.presentation.ChatsAdapter.ViewHolder.Companion.from

class ChatsAdapter(private val listener: ChatsAdapterListener, private val currentUser: User) :
    ListAdapter<Chat, ChatsAdapter.ViewHolder>(ChatDiffCallback) {
    interface ChatsAdapterListener {
        fun onChatClicked(chat: Chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentUser)
    }

    class ViewHolder(
        private val binding: ItemChatLayoutBinding,
        private val listener: ChatsAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat, currentUser: User) {
            binding.chat = chat
            binding.listener = listener
            binding.user = chat.users.find { user -> user.id != currentUser.id }
        }

        companion object {
            fun from(parent: ViewGroup, listener: ChatsAdapterListener) =
                ViewHolder(
                    ItemChatLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listener
                )
        }
    }
}