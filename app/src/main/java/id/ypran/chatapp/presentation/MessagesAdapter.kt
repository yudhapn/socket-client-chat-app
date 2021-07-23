package id.ypran.chatapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ypran.chatapp.data.Message
import id.ypran.chatapp.data.MessageDiffCallback
import id.ypran.chatapp.data.User
import id.ypran.chatapp.databinding.ItemMessageLeftLayoutBinding
import id.ypran.chatapp.databinding.ItemMessageRightLayoutBinding

private const val MSG_TYPE_LEFT = 0
private const val MSG_TYPE_RIGHT = 1

class MessagesAdapter(
    private val listener: MessagesAdapterListener,
    private val currentUser: User
) :
    ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback) {
    interface MessagesAdapterListener {
        fun onMessageLongClicked(message: Message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            MSG_TYPE_LEFT -> MessageLeftViewHolder.from(listener, parent)
            MSG_TYPE_RIGHT -> MessageRightViewHolder.from(listener, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is MessageLeftViewHolder -> holder.bind(getItem(position))
        is MessageRightViewHolder -> holder.bind(getItem(position))
        else -> throw java.lang.ClassCastException("Unknown viewHolder")
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).user.id == currentUser.id) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }

    class MessageLeftViewHolder(
        private val binding: ItemMessageLeftLayoutBinding,
        private val listener: MessagesAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.listener = listener
            binding.message = message

        }

        companion object {
            fun from(listener: MessagesAdapterListener, parent: ViewGroup) = MessageLeftViewHolder(
                ItemMessageLeftLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                listener
            )
        }
    }

    class MessageRightViewHolder(
        private val binding: ItemMessageRightLayoutBinding,
        private val listener: MessagesAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.listener = listener
            binding.message = message
        }

        companion object {
            fun from(listener: MessagesAdapterListener, parent: ViewGroup) = MessageRightViewHolder(
                ItemMessageRightLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                listener
            )

        }
    }

}