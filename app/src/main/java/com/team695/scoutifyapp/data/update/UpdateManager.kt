package com.team695.scoutifyapp.data.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

@SuppressLint("StaticFieldLeak")
object UpdateManager {
    const val APK_NAME = "scoutify_update.apk"
    var currentId: Long = 0
    lateinit var context: Context

    fun downloadUpdate(apkUrl: String): Long {
        val request = DownloadManager.Request(apkUrl.toUri())
            .setTitle("Scoutify Update")
            .setDescription("Downloading latest version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                APK_NAME
            )

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        currentId = manager.enqueue(request)

        return currentId
    }
}