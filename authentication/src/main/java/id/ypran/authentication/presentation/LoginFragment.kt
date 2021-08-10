package id.ypran.authentication.presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import id.ypran.authentication.R
import id.ypran.authentication.databinding.FragmentLoginBinding
import id.ypran.authentication.injectFeature
import id.ypran.authentication.util.BiometricPromptUtils
import id.ypran.authentication.util.CIPHERTEXT_WRAPPER
import id.ypran.authentication.util.CryptographyManager
import id.ypran.authentication.util.SHARED_PREFS_FILENAME
import id.ypran.chatapp.data.User
import id.ypran.chatapp.di.SHARED_USER_DATA
import id.ypran.chatapp.presentation.MainViewModel
import id.ypran.core.domain.ActiveUser
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val userPrefEditor: SharedPreferences.Editor by inject()
    private val viewModel: MainViewModel by sharedViewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            requireContext(),
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val observer = Observer<User> { user ->
        Log.d("LoginFragment", "$user")
        if (user.name.isNotEmpty()) {
            navigateToChats()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBiometricPromptForEncryption()
        viewModel.currentUser.observe(requireActivity(), observer)
    }

    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(requireContext())
            .canAuthenticate(BIOMETRIC_STRONG)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            binding.useBiometrics.visibility = View.VISIBLE
            binding.useBiometrics.setOnClickListener {
                if (ciphertextWrapper != null) {
                    showBiometricPromptForDecryption()
                } else {
                    val destination = LoginFragmentDirections.actionToEnableLoginBiometricFragment()
                    findNavController().navigate(destination)
                }
            }
        } else {
            binding.useBiometrics.visibility = View.INVISIBLE
        }

        if (ciphertextWrapper == null) {
            setupForLoginWithPassword()
        }
    }

    private fun setupForLoginWithPassword() {
        loginViewModel.loginWithPasswordFormState.observe(viewLifecycleOwner) { formState ->
            val loginState = formState ?: return@observe
            when (loginState) {
                is SuccessfulLoginFormState -> binding.loginButton.isEnabled =
                    loginState.isDataValid
                is FailedLoginFormState -> {
                    loginState.usernameError?.let { binding.usernameEditText.error = getString(it) }
                    loginState.passwordError?.let { binding.passwordEditText.error = getString(it) }
                }
            }
        }
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            val loginResult = result ?: return@observe
            if (loginResult.success) {
                navigateToChats()
            }
        }
        binding.usernameEditText.doAfterTextChanged { validateLoginForm() }
        binding.passwordEditText.doAfterTextChanged { validateLoginForm() }
        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> login()
            }
            false
        }
        binding.loginButton.setOnClickListener { login() }
    }

    private fun validateLoginForm() {
        loginViewModel.onLoginDataChanged(
            binding.usernameEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun login() {
        loginViewModel.login(
            binding.usernameEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
        val name = binding.usernameEditText.text.toString()
        val user = User(Random.nextInt(500), name)
        val userJson = Gson().toJson(user)
        userPrefEditor.putString(SHARED_USER_DATA, userJson).commit()
    }

    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName,
                textWrapper.initializationVector
            )
            biometricPrompt = BiometricPromptUtils.createBiometricPrompt(
                this,
                ::decryptServerTokenFromStorage
            )
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun decryptServerTokenFromStorage(authenticationResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authenticationResult.cryptoObject?.cipher?.let {
                val plaintext = cryptographyManager.decryptData(textWrapper.ciphertext, it)
                ActiveUser.fakeToken = plaintext
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