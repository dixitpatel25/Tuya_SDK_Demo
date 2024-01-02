package com.demo.beavair.fragment.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.demo.beavair.activity.LoginActivity
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import com.demo.beavair.utils.ToastUtil
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentAddHomeBinding
import com.example.tuyasdkdemo.databinding.FragmentHomeBinding
import com.thingclips.smart.android.user.api.ILogoutCallback
import com.thingclips.smart.home.sdk.ThingHomeSdk
import com.thingclips.smart.sdk.bean.DeviceBean
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var deviceListAdapter: DeviceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        deviceListAdapter = DeviceListAdapter(::onDeviceClicked)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting device list adapter
        binding.rvDevicesList.adapter = deviceListAdapter

        binding.imgLogout.setOnClickListener {
            // logout when user click
            Logout()
        }

        binding.btnAddHome.setOnClickListener {
            // redirect to user add home screen
            findNavController().navigate(R.id.action_homeFragment_to_addHomeFragment)
        }

        binding.btnAddDevice.setOnClickListener {
            // redirect to user add device screen
            findNavController().navigate(R.id.action_homeFragment_to_qrCodeConfigFragment)
        }

        binding.imgAddDevice.setOnClickListener {
            // redirect to user add device screen
            findNavController().navigate(R.id.action_homeFragment_to_qrCodeConfigFragment)
        }

        // Fetching home details using viewmodel
        homeViewModel.fetchCurrentHomeDetails(requireContext())

        bindObservers()
    }

    private fun bindObservers() {
        // Observing home details response
        homeViewModel.homeResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.homeBean == null) {
                        // home not added then showing no home found msg and layout
                        showLayoutNoHomeFound()
                    } else {
                        showHomeDetailLayout()
                        it.data.homeBean.let { bean ->
                            binding.txtHomeName.text = bean.name
                            if (bean.deviceList != null && bean.deviceList.size > 0) {
                                // device list found then showing device list
                                showDeviceFoundLayout()
                                deviceListAdapter.submitList(bean.deviceList)
                            } else {
                                // device list not found then showing add device list layout
                                showDeviceNotFoundLayout()
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {

                }
            }
        })
    }

    private fun onDeviceClicked(deviceBean: DeviceBean) {
        // user click on device list item then redirect to camera view screen
        if (deviceBean.isOnline) {
            val bundle = Bundle()
            bundle.putString(Constants.DEVICE_ID, deviceBean.devId)
            findNavController().navigate(R.id.action_homeFragment_to_cameraPanelFragment, bundle)
        } else {
            ToastUtil.shortToast(requireContext(), "Device is offline")
        }
    }

    private fun showLayoutNoHomeFound() {
        // No home Found then hide other layouts
        binding.linearHomeName.visibility = View.GONE
        binding.viewHome.visibility = View.GONE
        binding.linearNoDeviceFound.visibility = View.GONE
        binding.linearNoHomeFound.visibility = View.VISIBLE
        binding.linearDeviceList.visibility = View.GONE
    }

    private fun showHomeDetailLayout() {
        // home already added then show home Layouts
        binding.linearHomeName.visibility = View.VISIBLE
        binding.viewHome.visibility = View.VISIBLE
        binding.linearNoDeviceFound.visibility = View.GONE
        binding.linearNoHomeFound.visibility = View.GONE
        binding.linearDeviceList.visibility = View.GONE
    }

    private fun showDeviceNotFoundLayout() {
        // device not found
        binding.linearHomeName.visibility = View.VISIBLE
        binding.viewHome.visibility = View.VISIBLE
        binding.linearNoDeviceFound.visibility = View.VISIBLE
        binding.linearNoHomeFound.visibility = View.GONE
        binding.linearDeviceList.visibility = View.GONE
    }

    private fun showDeviceFoundLayout() {
        // device found show list
        binding.linearHomeName.visibility = View.VISIBLE
        binding.viewHome.visibility = View.VISIBLE
        binding.linearNoDeviceFound.visibility = View.GONE
        binding.linearNoHomeFound.visibility = View.GONE
        binding.linearDeviceList.visibility = View.VISIBLE
    }

    private fun Logout() {
        //Logout
        ThingHomeSdk.getUserInstance().logout(object : ILogoutCallback {
            override fun onSuccess() {
                Log.d(Constants.TAG, "Logout Success")
                SecurePreferences.clearSecurepreference(requireContext())
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }

            override fun onError(code: String?, error: String?) {

            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}