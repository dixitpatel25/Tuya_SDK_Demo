package com.demo.beavair.fragment.qrcode

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.beavair.models.WifiRequest
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.DensityUtil
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.QRCodeUtil
import com.demo.beavair.utils.SecurePreferences
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentHomeBinding
import com.example.tuyasdkdemo.databinding.FragmentQrCodeConfigBinding
import com.google.gson.Gson
import com.google.zxing.WriterException
import com.thingclips.smart.home.sdk.ThingHomeSdk
import com.thingclips.smart.home.sdk.builder.ThingCameraActivatorBuilder
import com.thingclips.smart.sdk.api.IThingCameraDevActivator
import com.thingclips.smart.sdk.api.IThingSmartCameraActivatorListener
import com.thingclips.smart.sdk.bean.DeviceBean
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrCodeConfigFragment : Fragment() {

    private var _binding: FragmentQrCodeConfigBinding? = null
    private val binding get() = _binding!!


    private var mTuyaActivator: IThingCameraDevActivator? =null
    private val deviceConfingViewModel by activityViewModels<DeviceConfingViewModel>()
    private var wifiRequest: WifiRequest? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrCodeConfigBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val wifiName = binding.edtWifeName.text.toString()
            val wifiPassword = binding.edtWifiPassword.text.toString()
            // checking validation user input
            val validationResult = validateUserInput(wifiName, wifiPassword)
            if (validationResult.first) {
                wifiRequest = WifiRequest(wifiName, wifiPassword)
                // fetching token using viewmodel
                deviceConfingViewModel.fetchToken(requireContext())
            } else {
                Toast.makeText(requireActivity(), validationResult.second, Toast.LENGTH_LONG).show()
            }
        }

        bindObservers()
    }

    private fun bindObservers() {
        // Observing token response
        deviceConfingViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let { result ->
                        if (result.first) {
                            // once token fetch successfully then request fetch qrCode Url
                            fetchQrCodeUrl(result.second)
                        } else {
                            Toast.makeText(requireActivity(), result.second, Toast.LENGTH_LONG).show()
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

    fun fetchQrCodeUrl(token: String) {
        //Create and show qrCode
        val builder = ThingCameraActivatorBuilder()
            .setToken(token)
            .setPassword(wifiRequest?.wifiPassword)
            .setTimeOut(100)
            .setContext(context)
            .setSsid(wifiRequest?.wifiName)
            .setListener(object : IThingSmartCameraActivatorListener {
                override fun onQRCodeSuccess(qrcodeUrl: String) {
                    // once qecodeurl get then generating bitmap for show user
                    val bitmap: Bitmap
                    try {
                        val widthAndHeight: Int =
                            DensityUtil.getScreenDispalyWidth(requireContext()) - DensityUtil.dip2px(
                                requireContext(),
                                50f
                            )
                        bitmap = QRCodeUtil.createQRCode(qrcodeUrl, widthAndHeight)
                        // add coroutines here bcoz sometime it carsh mainlooper error get
                        lifecycleScope.launch {
                            binding.linearWifeDetails.visibility = View.GONE
                            binding.cardQrCode.visibility = View.VISIBLE
                            binding.imgQrCode.setImageBitmap(bitmap)
                        }
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(errorCode: String, errorMsg: String) {
                    Toast.makeText(requireActivity(), "$errorCode --> $errorMsg", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onActiveSuccess(devResp: DeviceBean) {
                    // device successfully scan
                    Toast.makeText(requireActivity(), "config success!", Toast.LENGTH_LONG).show()
                    // redirect to user back screen
                    findNavController().popBackStack()
                }
            })
        mTuyaActivator = ThingHomeSdk.getActivatorInstance().newCameraDevActivator(builder)
        mTuyaActivator?.createQRCode()
        mTuyaActivator?.start()
    }

    private fun validateUserInput(wifiName: String, wifiPassword: String): Pair<Boolean, String> {
        return deviceConfingViewModel.checkValidation(wifiName, wifiPassword)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mTuyaActivator?.stop()
        mTuyaActivator?.onDestroy()
    }
}