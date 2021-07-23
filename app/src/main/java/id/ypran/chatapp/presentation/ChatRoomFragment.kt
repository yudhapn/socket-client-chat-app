package id.ypran.chatapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.ypran.chatapp.data.Chat
import id.ypran.chatapp.data.Message
import id.ypran.chatapp.data.User
import id.ypran.chatapp.databinding.FragmentChatRoomBinding
import id.ypran.chatapp.presentation.MessagesAdapter.MessagesAdapterListener

class ChatRoomFragment : Fragment(), MessagesAdapterListener {
    private lateinit var binding: FragmentChatRoomBinding
    private val arg: ChatRoomFragmentArgs by navArgs()
    private val currentUser = User(1, "ypran")
    private val chat: Chat by lazy { arg.chatArg }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initView()
    }

    private fun initView() {
        binding.chatRoomToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.user = chat.users.find { user -> user.id != currentUser.id }
    }

    private fun initRecyclerView() {
//        val chat = arg.chatArg
        val adapter = MessagesAdapter(this@ChatRoomFragment, currentUser)
        binding.messageRecyclerView.adapter = adapter
        adapter.submitList(chat.messages)
    }

    override fun onMessageLongClicked(message: Message) {
        Toast.makeText(requireContext(), message.text, Toast.LENGTH_SHORT).show()
    }
}