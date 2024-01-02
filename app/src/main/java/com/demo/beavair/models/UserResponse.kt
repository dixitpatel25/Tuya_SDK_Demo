package com.demo.beavair.models

import com.thingclips.smart.android.user.bean.User

data class UserResponse(
    val message: String,
    val user: User? = null
)
