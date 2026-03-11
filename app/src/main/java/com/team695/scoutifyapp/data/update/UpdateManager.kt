package com.team695.scoutifyapp.data.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val APK_URL = ""

@SuppressLint("StaticFieldLeak")
object UpdateManager {
    suspend fun downloadUpdate(context: Context): Long {
        return withContext(Dispatchers.IO) {
            val request = DownloadManager.Request(APK_URL.toUri())
                .setTitle("Scoutify Update")
                .setDescription("Downloading latest version...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(
                    context,
                    Environment.DIRECTORY_DOWNLOADS, "scoutify_update.apk"
                )

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            return@withContext manager.enqueue(request)
        }
    }
}