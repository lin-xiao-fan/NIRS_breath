package com.example.brelax

import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
data class DeviceInfo(
    val name: String,
    val address: String,
    val extraInfo: Map<String, Any>
)