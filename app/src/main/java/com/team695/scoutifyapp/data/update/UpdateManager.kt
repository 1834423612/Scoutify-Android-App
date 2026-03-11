package com.team695.scoutifyapp.data.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

const val APK_URL = ""

@SuppressLint("StaticFieldLeak")
object UpdateManager {
    fun downloadUpdate(context: Context): Long {
        val request = DownloadManager.Request(APK_URL.toUri())
            .setTitle("Scoutify Update")
            .setDescription("Downloading latest version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS, "scoutify_update.apk"
            )

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return manager.enqueue(request)
    }
}