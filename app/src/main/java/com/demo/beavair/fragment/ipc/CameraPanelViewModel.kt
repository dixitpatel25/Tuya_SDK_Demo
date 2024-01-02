package com.demo.beavair.fragment.ipc

import android.database.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.demo.beavair.repository.UserRepository
import com.thingclips.smart.camera.ipccamerasdk.p2p.ICameraP2P
import javax.inject.Inject

class CameraPanelViewModel @Inject constructor() : ViewModel(){
     var previewMute = ObservableField<Int>(ICameraP2P.MUTE)
     var videoClarity = ObservableField<Int>(ICameraP2P.HD)
}