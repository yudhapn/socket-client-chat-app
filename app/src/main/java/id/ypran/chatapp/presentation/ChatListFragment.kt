package id.ypran.chatapp.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.ypran.chatapp.data.Chat
import id.ypran.chatapp.data.Message
import id.ypran.chatapp.data.User
import id.ypran.chatapp.databinding.FragmentChatListBinding
import id.ypran.chatapp.di.SOCKET_SCOPE_ID
import id.ypran.chatapp.di.SOCKET_SESSION_QUALIFIER
import id.ypran.chatapp.presentation.ChatsAdapter.ChatsAdapterListener
import id.ypran.chatapp.presentation.UsersAdapter.UsersAdapterListener
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

private const val USERS = "users"

class ChatListFragment : Fragment(), ChatsAdapterListener, UsersAdapterListener,
    AndroidScopeComponent {
    private lateinit var binding: FragmentChatListBinding
    private val viewModel: MainViewModel by viewModel()
    private val currentUser = User(1, "ypran")
    private lateinit var socket: Socket
    override val scope: Scope by fragmentScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentUser.observe(requireActivity(), { user ->
            Log.d("ChatListFragment", "$user")
        })
        initRecyclerView()
        initSocket()
    }

    private fun initRecyclerView() {
        with(binding) {
            val chatsAdapter = ChatsAdapter(this@ChatListFragment, currentUser)
            chatRecyclerView.adapter = chatsAdapter
            chatsAdapter.submitList(getChats())
            val usersAdapter = UsersAdapter(this@ChatListFragment)
            userRecyclerView.adapter = usersAdapter
            usersAdapter.submitList(
                listOf(
                    User(2, "elvin"),
                    User(3, "rakha")
                )
            )
        }
    }

    private fun initSocket() {
        val socketScope =
            getKoin().getOrCreateScope(SOCKET_SCOPE_ID, named(SOCKET_SESSION_QUALIFIER))
        scope.linkTo(socketScope)
        socket = get()
        Log.d("ChatListFragment", "socketId: ${socket.id()}")
        startListening()
    }

    private val usersListener = Emitter.Listener {
        Log.d("ChatListFragment", "${it.size}")
        Log.d("ChatListFragment", "${it[0]}")
//        val data = JSONObject(it[0].toString())
        val userId: String
        val name: String
//        try {
//            userId = data.getString("userId")
//            name = data.getString("name")
//            Log.d("ChatListFragment", "userId: $userId, name: $name")
//        } catch (e: Exception) {
//            Log.d("ChatListFragment", "${e.message}")
//        }
    }

    private fun startListening() {
        socket.on(USERS, usersListener)
    }

    private fun getChats(): List<Chat> {
        val user1 = User(2, "elvin")
        val user2 = User(3, "rakha")
        val messages1 = listOf(
            Message(1, "hello", currentUser),
            Message(
                1,
                "apa kabar, kemaren gua abis pergi ke pantai pandawa bareng saddam. Asik banget deh!",
                user1
            ),
        )
        val messages2 = listOf(
            Message(
                1,
                "hello, I\'m yudha, I'm graduated from braw university with major in informatics engineer",
                currentUser
            ),
            Message(1, "how are you?", user2),
        )
        return listOf(
            Chat(id, listOf(currentUser, user1), messages1, messages1.last()),
            Chat(id, listOf(currentUser, user2), messages2, messages2.last()),
        )
    }

    override fun onChatClicked(chat: Chat) {
        val direction = ChatListFragmentDirections.actionToChatRoomFragment(chat)
        findNavController().navigate(direction)
    }

    override fun onUserClicked(user: User) {
        val chat = getChats().find { chat -> chat.users.contains(user) } ?: Chat(
            3,
            listOf(user, currentUser),
            listOf(),
            Message(1, "", currentUser)
        )
        val direction = ChatListFragmentDirections.actionToChatRoomFragment(chat)
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }
}