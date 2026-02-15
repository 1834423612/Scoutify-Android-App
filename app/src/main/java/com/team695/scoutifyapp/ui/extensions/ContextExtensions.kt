package com.team695.scoutifyapp.ui.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

val Context.deviceId: String @SuppressLint("HardwareIds")
get() {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}