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
import com.demo.beavair.utils.Helper.Companion.hideKeyboard
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentLoginBinding
import com.example.tuyasdkdemo.databinding.FragmentRegisterBinding
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog.Companion.setOnCountrySelected
import com.fasilthottathil.countryselectiondialog.CountrySelectionDialog.Companion.show
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var countryCode = "+91"

    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            // redirect to user login screen
            val navController = findNavController(requireActivity(), R.id.nav_login_host_fragment)
            navController.popBackStack()
        }

        binding.txtCountry.setOnClickListener {
            //open country picker dialog for select country
            CountrySelectionDialog().create(requireActivity())
                .show()?.setOnCountrySelected {
                    countryCode = it.code
                    binding.txtCountry.text = it.name
                }
        }

        binding.btnSendCode.setOnClickListener {
            // send verification code button click
            hideKeyboard(it)
            val validationResult = validateUserEmail()
            if (validationResult.first) {
                // calling send verification api using viewmodel
                val userRequest = getUserRequest()
                authViewModel.sendVerificationCode(userRequest)
            }else{
                showValidationErrors(validationResult.second)
            }

        }

        binding.btnSignUp.setOnClickListener {
            if(binding.btnSignUp.isEnabled){
                hideKeyboard(it)
                val validationResult = validateUserInput()
                if (validationResult.first) {
                    val userRequest = getUserRequest()
                    // calling register api using viewmodel
                    authViewModel.registerUser(userRequest)
                }else{
                    showValidationErrors(validationResult.second)
                }
            }
        }

        bindObservers()

    }

    private fun showValidationErrors(error: String) {
        // for show errors
        binding.txtError.text = String.format(resources.getString(R.string.txt_error_message, error))
    }

    private fun validateUserEmail(): Pair<Boolean, String> {
        // checking email validation
        val emailAddress = binding.txtEmail.text.toString()
        return authViewModel.validateEmail(emailAddress)
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        // checking validation using viewmodel
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val verifyCode = binding.txtVerificationCode.text.toString()
        return authViewModel.validateCredentialsForRegister(emailAddress, password,verifyCode)
    }

    private fun getUserRequest(): UserRequest {
        return binding.run {
            UserRequest(
                countryCode,
                txtEmail.text.toString(),
                txtPassword.text.toString(),
                txtVerificationCode.text.toString()
            )
        }
    }

    private fun bindObservers() {
        // Observing verification code and signup response
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(requireActivity(), it.data?.message, Toast.LENGTH_LONG).show()
                    if(it.data?.user == null){
                        binding.btnSignUp.isEnabled = true
                        binding.btnSignUp.alpha = 1f
                    }else{
                        // save user data in shared preference
                        SecurePreferences.savePreferences(requireContext(),Constants.USER_MODEL, Gson().toJson(it.data.user))
                        // redirect to user home screen
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }
                is NetworkResult.Loading ->{
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