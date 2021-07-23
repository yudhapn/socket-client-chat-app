package id.ypran.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ypran.chatapp.di.SOCKET_SCOPE_ID
import id.ypran.chatapp.presentation.MainViewModel
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

class MainActivity : AppCompatActivity(){

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
//        val socketSession =
//            getKoin().getOrCreateScope(SOCKET_SCOPE_ID, named(SOCKET_SESSION_QUALIFIER))
//        scope.linkTo(socketSession)
//        val socket = get<Socket>()
//        socket.disconnect()
//        scope.close()
    }
}