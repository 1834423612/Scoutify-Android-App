package com.team695.scoutifyapp.data.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

const val APK_URL = "https://www.dropbox.com/scl/fi/2mncyhhjibl5u54azv54q/app-release.apk?rlkey=edgfc4nyxo8mszolk36pz3r1f&st=6fif6nnf&dl=1"
const val APK_NAME = "scoutify_update.apk"
@SuppressLint("StaticFieldLeak")
object UpdateManager {

    var currentId: Long = 0
    lateinit var context: Context
    fun downloadUpdate(): Long {
        val request = DownloadManager.Request(APK_URL.toUri())
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