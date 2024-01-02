package com.demo.beavair.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import com.demo.beavair.activity.MainActivity
import com.demo.beavair.models.UserRequest
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.Helper
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentLoginBinding
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog.Companion.setOnCountrySelected
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog.Companion.show
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var countryCode = "+91"
    private val authViewModel by activityViewModels<AuthViewModel>()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSignUp.setOnClickListener {
            // redirect user to sign up screen
            val navController = findNavController(requireActivity(), R.id.nav_login_host_fragment)
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.txtCountry.setOnClickListener {
            // open country picker dialog
            CountrySelectionDialog().create(requireActivity())
                .show()?.setOnCountrySelected {
                    countryCode = it.code
                    binding.txtCountry.text = it.name
                }
        }

        binding.btnLogin.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                // calling api using viewmodel
                val userRequest = getUserRequest()
                authViewModel.loginUser(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()
    }

    private fun showValidationErrors(error: String) {
        // for show errors
        binding.txtError.text =
            String.format(resources.getString(R.string.txt_error_message, error))
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        // checking validation
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return authViewModel.validateCredentialsForLogin(emailAddress, password)
    }

    private fun getUserRequest(): UserRequest {
        // create user request
        return binding.run {
            UserRequest(
                countryCode,
                txtEmail.text.toString(),
                txtPassword.text.toString(),
                ""
            )
        }
    }

    private fun bindObservers() {
        // Observing login response
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data == null) {
                        Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                    } else {
                        // save user data in shared preference
                        SecurePreferences.savePreferences(requireContext(), Constants.USER_MODEL, Gson().toJson(it.data.user))
                        // redirect to user home screen
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}