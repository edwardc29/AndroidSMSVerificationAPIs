package com.carrion.edward.smsverificationapp.util

object Util {
    fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }
}