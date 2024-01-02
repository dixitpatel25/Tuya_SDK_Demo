package com.demo.beavair.models

data class UserRequest(
    val countryCode: String,
    val email: String,
    val password: String,
    val verificationCode: String
)
