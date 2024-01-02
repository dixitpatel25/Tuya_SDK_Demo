package com.demo.beavair.models

import com.thingclips.smart.android.user.bean.User
import com.thingclips.smart.home.sdk.bean.HomeBean

data class HomeResponse(
    val type: String,
    val homeBean: HomeBean? = null
)
