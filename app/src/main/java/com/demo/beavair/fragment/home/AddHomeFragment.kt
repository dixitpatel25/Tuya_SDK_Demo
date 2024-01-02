package com.demo.beavair.fragment.home

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.demo.beavair.utils.NetworkResult
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentAddHomeBinding
import com.example.tuyasdkdemo.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddHomeFragment : Fragment() {

    private var _binding: FragmentAddHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearBack.setOnClickListener {
            // user clicked on back button redirect home screen
            findNavController().popBackStack()
        }

        binding.btnAddHome.setOnClickListener {
            // add home button click
            val homeName = binding.edtHomeName.text.toString()
            if((TextUtils.isEmpty(homeName))){
                Toast.makeText(requireActivity(), "Please provide home name", Toast.LENGTH_LONG).show()
            }else{
                // create home api calling using viewmodel
                homeViewModel.createNewHome(requireContext(),homeName)
            }
        }
        bindObservers()
    }

    private fun bindObservers() {
        // Observing add home response
        homeViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    // add home successfully then redirect to home screen
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}