package id.ypran.authentication.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.ypran.authentication.R
import id.ypran.authentication.databinding.FragmentEnableBiometricLoginBinding
import id.ypran.authentication.injectFeature
import id.ypran.authentication.util.BiometricPromptUtils
import id.ypran.authentication.util.CIPHERTEXT_WRAPPER
import id.ypran.authentication.util.CryptographyManager
import id.ypran.authentication.util.SHARED_PREFS_FILENAME
import id.ypran.core.domain.ActiveUser
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableBiometricLoginFragment : Fragment() {
    private val TAG = "EnableBiometricLogin"
    private lateinit var cryptographyManager: CryptographyManager
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentEnableBiometricLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnableBiometricLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForLoginWithPassword()
    }

    private fun setupForLoginWithPassword() {
        loginViewModel.loginWithPasswordFormState.observe(viewLifecycleOwner) { formState ->
            Log.d(TAG, "loginWithPasswordFormState $formState")
            val loginState = formState ?: return@observe
            when (loginState) {
                is SuccessfulLoginFormState -> binding.authorizeButton.isEnabled =
                    loginState.isDataValid
                is FailedLoginFormState -> {
                    loginState.usernameError?.let { binding.usernameEditText.error = getString(it) }
                    loginState.passwordError?.let { binding.passwordEditText.error = getString(it) }
                }
            }
        }
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "loginResult $result")
            val loginResult = result ?: return@observe
            if (loginResult.success) showBiometricPromptForEncryption()
        }
        binding.usernameEditText.doAfterTextChanged { validateLoginForm() }
        binding.passwordEditText.doAfterTextChanged { validateLoginForm() }
        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> authorize()
            }
            false
        }
        binding.authorizeButton.setOnClickListener { authorize() }
        binding.cancelButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun validateLoginForm() {
        loginViewModel.onLoginDataChanged(
            binding.usernameEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun authorize() {
        loginViewModel.login(
            binding.usernameEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(requireContext())
            .canAuthenticate(BIOMETRIC_STRONG)
        Log.d(TAG, "canAuthenticate $canAuthenticate")
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authenticationResult: BiometricPrompt.AuthenticationResult) {
        authenticationResult.cryptoObject?.cipher?.apply {
            ActiveUser.fakeToken?.let { token ->
                Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    requireContext(),
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val direction = EnableBiometricLoginFragmentDirections.actionToLoginFragment()
        findNavController().navigate(direction)
    }
}