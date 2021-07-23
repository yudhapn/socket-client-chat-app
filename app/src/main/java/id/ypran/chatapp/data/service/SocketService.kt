package id.ypran.chatapp.data.service

import android.util.Log
import id.ypran.chatapp.data.User
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.get
import org.koin.core.scope.Scope

private const val USERS = "users"

class SocketService(private val currentUser: User) : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
    private var socket: Socket? = null

    init {
        socket = get()
    }

    private val usersListener = Emitter.Listener {
        Log.d("ChatListFragment", "${it.size}")
        Log.d("ChatListFragment", "${it[0]}")
    }

    fun startListening() {
        socket?.on(USERS, usersListener)
        socket?.connect()
    }

    fun stopListening() {
        socket?.disconnect()
        scope.close()
    }
}