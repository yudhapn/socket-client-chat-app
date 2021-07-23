package id.ypran.chatapp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import id.ypran.chatapp.data.User
import id.ypran.chatapp.databinding.FragmentLoginBinding
import id.ypran.chatapp.di.SHARED_USER_DATA
import id.ypran.chatapp.presentation.MainViewModel
import io.socket.client.Socket
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.random.Random

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val userPrefEditor: SharedPreferences.Editor by inject()
    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    val observer = Observer<User> { user ->
        Log.d("LoginFragment", "$user")
        if (user.name.isNotEmpty()) {
            navigateToChats()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.currentUser.observe(requireActivity(), observer)

    }

    private fun initView() {
        with(binding) {
            loginButton.setOnClickListener {
                val name = usernameEditText.text.toString()
                val user = User(Random.nextInt(500), name)
                val userJson = Gson().toJson(user)
                userPrefEditor.putString(SHARED_USER_DATA, userJson).commit()
                navigateToChats()
            }
        }
    }

    private fun navigateToChats() {
        val direction = LoginFragmentDirections.actionToChatListFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.currentUser.removeObserver(observer)
    }
}