package com.demo.beavair.fragment.ipc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.WINDOW_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.MessageUtil
import com.demo.beavair.utils.ToastUtil
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.FragmentCameraPanelBinding
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.AbsP2pCameraListener
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack
import com.thingclips.smart.camera.ipccamerasdk.p2p.ICameraP2P
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P
import com.thingclips.smart.camera.middleware.widget.AbsVideoViewCallback
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
class CameraPanelFragment : Fragment(),CameraPanelViewClicks {

    private var _binding: FragmentCameraPanelBinding? = null
    private val binding get() = _binding!!

    private var devId: String? = null
    private var mCameraP2P: IThingSmartCameraP2P<Any>? = null

    private var isPlay = false
    var reConnect = false
    private var isSpeaking = false
    private var isRecording = false

    lateinit var cameraPanelViewModel: CameraPanelViewModel;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraPanelBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVideoViewHight()
        initData()
    }

    private fun setVideoViewHight() {
        val windowManager = requireActivity().getSystemService(WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width
        val height = width * ASPECT_RATIO_WIDTH / ASPECT_RATIO_HEIGHT
        val layoutParams = RelativeLayout.LayoutParams(width, height)
        binding.relativeCameraView.layoutParams = layoutParams
    }

    private fun initData() {

        cameraPanelViewModel=ViewModelProvider(this).get(CameraPanelViewModel::class.java)
        binding.viewModel=cameraPanelViewModel
        binding.clicks = this
        binding.cameraControlBoard.clicks = this

        devId = arguments?.getString(Constants.DEVICE_ID)

        ThingIPCSdk.getCameraInstance()?.let {
            mCameraP2P = it.createCameraP2P(devId)
        }

        binding.cameraVideoView.setViewCallback(object : AbsVideoViewCallback() {
            override fun onCreated(o: Any) {
                super.onCreated(o)
                mCameraP2P?.generateCameraView(o)
            }
        })

        binding.cameraVideoView.createVideoView(devId)

        if (mCameraP2P == null)
            showNotSupportToast()

    }

    private fun showNotSupportToast() {
        Toast.makeText(requireActivity(), getString(R.string.not_support_device), Toast.LENGTH_LONG).show()
    }

    override fun camMuteClick() {
        mCameraP2P?.let {
            val mute = if (cameraPanelViewModel.previewMute.get() == ICameraP2P.MUTE) ICameraP2P.UNMUTE else ICameraP2P.MUTE
            mCameraP2P?.setMute(mute, object : OperationDelegateCallBack {
                override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                    cameraPanelViewModel.previewMute.set(Integer.valueOf(data))
                }

                override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                    Log.d(Constants.TAG,getString(R.string.operation_failed))
                }
            })
        }
    }

    private fun preview() {
        cameraPanelViewModel.videoClarity.get()?.let {
            mCameraP2P?.startPreview(it, object : OperationDelegateCallBack {
                override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                    Log.d(TAG, "start preview onSuccess")
                    isPlay = true
                }

                override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                    Log.d(TAG, "start preview onFailure, errCode: $errCode")
                    isPlay = false
                }
            })
        }
    }

    override fun setVideoClarity() {
        mCameraP2P?.let {
            mCameraP2P?.setVideoClarity(
                if (cameraPanelViewModel.videoClarity.get() == ICameraP2P.HD) ICameraP2P.STANDEND else ICameraP2P.HD,
                object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                        cameraPanelViewModel.videoClarity.set( Integer.valueOf(data))
                        Log.d(Constants.TAG,"Video clarity change")
                    }

                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                        Log.d(Constants.TAG,"Video clarity not change")
                    }
                })
        }

    }

    override fun speakClick() {
        mCameraP2P?.let {
            if (isSpeaking) {
                mCameraP2P?.stopAudioTalk(object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                        isSpeaking = false
                        lifecycleScope.launch {
                            ToastUtil.shortToast(
                                requireContext(),
                                getString(R.string.ipc_stop_talk) + getString(R.string.operation_suc)
                            )
                        }
                    }

                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                        isSpeaking = false
                        lifecycleScope.launch {
                            ToastUtil.shortToast(requireContext(), getString(R.string.ipc_stop_talk) + getString(R.string.operation_failed))
                        }
                    }
                })
            }
            else {
                if (Constants.hasRecordPermission()) {
                    mCameraP2P?.startAudioTalk(object : OperationDelegateCallBack {
                        override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                            isSpeaking = true
                            lifecycleScope.launch {
                                ToastUtil.shortToast(
                                    requireContext(),
                                    getString(R.string.ipc_stop_talk) + getString(R.string.operation_suc)
                                )
                            }
                        }

                        override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                            isSpeaking = false
                            lifecycleScope.launch {
                                ToastUtil.shortToast(
                                    requireContext(),
                                    getString(R.string.ipc_stop_talk) + getString(R.string.operation_failed)
                                )
                            }
                        }
                    })
                } else {
                    Constants.requestPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO,
                        Constants.EXTERNAL_AUDIO_REQ_CODE,
                        "open_recording"
                    )
                }
            }
        }
    }

    override fun recordClick() {
        mCameraP2P?.let {
            if (!isRecording) {
                val picPath = requireActivity().getExternalFilesDir(null)!!.path + "/" + devId
                val file = File(picPath)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val fileName = System.currentTimeMillis().toString() + ".mp4"
                mCameraP2P?.startRecordLocalMp4(
                    picPath,
                    fileName,
                    requireContext(),
                    object : OperationDelegateCallBack {
                        override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                            isRecording = true
                            lifecycleScope.launch {
                                ToastUtil.shortToast(requireContext(), getString(R.string.operation_suc))
                            }

                            Log.i(TAG, "record :$data")
                        }

                        override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                            lifecycleScope.launch {
                                ToastUtil.shortToast(requireContext(), getString(R.string.operation_failed))
                            }
                        }
                    })
                recordStatue(true)
            }
            else {
                mCameraP2P?.stopRecordLocalMp4(object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                        isRecording = false
                        lifecycleScope.launch {
                            ToastUtil.shortToast(requireContext(), getString(R.string.operation_suc))
                        }
                    }

                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                        isRecording = false
                        lifecycleScope.launch {
                            ToastUtil.shortToast(requireContext(), getString(R.string.operation_failed))
                        }

                    }
                })
                recordStatue(false)
            }
        }

    }

    private fun recordStatue(isRecording: Boolean) {
        binding.cameraControlBoard.speakTxt.isEnabled = !isRecording
        binding.cameraControlBoard.photoTxt.isEnabled = !isRecording
        binding.cameraControlBoard.replayTxt.isEnabled = !isRecording
        binding.cameraControlBoard.recordTxt.isEnabled = true
        binding.cameraControlBoard.recordTxt.isSelected = isRecording
    }

    override fun snapShotClick() {
        mCameraP2P?.let {
            val picPath = requireActivity().getExternalFilesDir(null)!!.path + "/" + devId
            val file = File(picPath)
            if (!file.exists()) file.mkdirs()
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            mCameraP2P?.snapshot(
                picPath,
                fileName,
                requireContext(),
                object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                        Log.i(TAG, "snapshot :$data")
                        ToastUtil.shortToast(requireContext(), getString(R.string.operation_suc))
                    }

                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                        ToastUtil.shortToast(requireContext(), getString(R.string.operation_failed))
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cameraVideoView.onResume()
        //must register again,or can't callback
        mCameraP2P?.let {
            it.registerP2PCameraListener(p2pCameraListener)
            it.generateCameraView(binding.cameraVideoView.createdView())
            if (it.isConnecting) {
                it.startPreview(object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {
                        isPlay = true
                    }

                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {
                        Log.d(TAG, "start preview onFailure, errCode: $errCode")
                    }
                })
            } else {
                if (ThingIPCSdk.getCameraInstance()?.isLowPowerDevice(devId) == true) {
                    ThingIPCSdk.getDoorbell()?.wirelessWake(devId)
                }
                //Establishing a p2p channel
                it.connect(devId, object : OperationDelegateCallBack {
                    override fun onSuccess(i: Int, i1: Int, s: String) {
                        preview()
                    }

                    override fun onFailure(i: Int, i1: Int, i2: Int) {
                        ToastUtil.shortToast(requireContext(), getString(R.string.connect_failed))
                    }
                })
            }
        }
    }

    private val p2pCameraListener: AbsP2pCameraListener = object : AbsP2pCameraListener() {
        override fun onReceiveSpeakerEchoData(pcm: ByteBuffer, sampleRate: Int) {
            mCameraP2P?.let {
                val length = pcm.capacity()
                Log.d(TAG, "receiveSpeakerEchoData pcmlength $length sampleRate $sampleRate")
                val pcmData = ByteArray(length)
                pcm[pcmData, 0, length]
                it.sendAudioTalkData(pcmData, length)
            }
        }

        override fun onSessionStatusChanged(camera: Any?, sessionId: Int, sessionStatus: Int) {
            super.onSessionStatusChanged(camera, sessionId, sessionStatus)
            if (sessionStatus == -3 || sessionStatus == -105) {
                // 遇到超时/鉴权失败，建议重连一次，避免循环调用
                if (!reConnect) {
                    reConnect = true
                    mCameraP2P?.connect(devId, object : OperationDelegateCallBack {
                        override fun onSuccess(i: Int, i1: Int, s: String) {
                            preview()
                        }

                        override fun onFailure(i: Int, i1: Int, i2: Int) {
                            ToastUtil.shortToast(requireContext(), getString(R.string.connect_failed))
                        }
                    })
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.cameraVideoView.onPause()
        mCameraP2P?.let {
            if (isSpeaking) it.stopAudioTalk(null)
            if (isPlay) {
                it.stopPreview(object : OperationDelegateCallBack {
                    override fun onSuccess(sessionId: Int, requestId: Int, data: String) {}
                    override fun onFailure(sessionId: Int, requestId: Int, errCode: Int) {}
                })
                isPlay = false
            }
            it.removeOnP2PCameraListener()
            it.disconnect(object : OperationDelegateCallBack {
                override fun onSuccess(i: Int, i1: Int, s: String) {}
                override fun onFailure(i: Int, i1: Int, i2: Int) {}
            })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mCameraP2P?.destroyP2P()
    }

    companion object {
        private const val ASPECT_RATIO_WIDTH = 9
        private const val ASPECT_RATIO_HEIGHT = 16
        private const val TAG = "CameraPanelFragment"
    }



}