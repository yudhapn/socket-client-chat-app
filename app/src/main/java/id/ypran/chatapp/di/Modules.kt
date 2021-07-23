package id.ypran.chatapp.di

import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import id.ypran.chatapp.data.User
import id.ypran.chatapp.data.repository.UserRepositoryImpl
import id.ypran.chatapp.data.service.SocketService
import id.ypran.chatapp.domain.GetCurrentUserCacheUseCase
import id.ypran.chatapp.domain.UserRepository
import id.ypran.chatapp.presentation.ChatListFragment
import id.ypran.chatapp.presentation.MainViewModel
import io.socket.client.IO
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.net.URI

private const val SHARED_USER = "shared_user"
const val SHARED_USER_DATA = "shared_user_data"
const val SOCKET_SESSION_QUALIFIER = "socket_session"
const val SOCKET_SCOPE_ID = "socket_scope"

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
}

val repositoryModule = module {
    factory {
        UserRepositoryImpl(get()) as UserRepository
    }
}

val useCaseModule = module {
    factory {
        GetCurrentUserCacheUseCase(get())
    }
}

val serviceModule = module {
    single {
//        SocketService()
    }
}

val socketModule = module {
    scope<ChatListFragment> { }
    scope<SocketService> {
        scoped {
            val uri = URI.create("https://chat-app-borax-socket.herokuapp.com/")
            val user: User = get()
            val options = IO.Options.builder()
                .setAuth(
                    mapOf(
                        "userId" to user.id.toString(),
                        "name" to user.name
                    )
                )
                .build()
            val socket = IO.socket(uri, options)
            socket.connect()
            socket
        }
    }
    scope(named(SOCKET_SESSION_QUALIFIER)) {
        scoped {
            val uri = URI.create("https://chat-app-borax-socket.herokuapp.com/")
            val user: User = get()
            val options = IO.Options.builder()
                .setAuth(
                    mapOf(
                        "userId" to user.id.toString(),
                        "name" to user.name
                    )
                )
                .build()
            val socket = IO.socket(uri, options)
            socket.connect()
            socket
        }
    }
}

val preferenceModule = module {
    factory {
        val sharedPreferences = androidContext().getSharedPreferences(SHARED_USER, MODE_PRIVATE)
        val userJson = sharedPreferences.getString(SHARED_USER_DATA, "")
        userJson?.let {
            Gson().fromJson(userJson, User::class.java)
        } ?: User(-1, "")
    }

    factory {
        val prefEditor = androidContext().getSharedPreferences(SHARED_USER, MODE_PRIVATE).edit()
        prefEditor
    }
}

val appComponent =
    listOf(
        socketModule,
        preferenceModule,
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )