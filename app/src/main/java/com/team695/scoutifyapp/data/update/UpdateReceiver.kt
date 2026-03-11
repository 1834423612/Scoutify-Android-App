package com.team695.scoutifyapp.data.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class UpdateReceiver(
    private val onFail: () -> Long?
): BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("UpdateReceiver", "Received file!")
        if (context == null || intent == null) {
            Log.d("UpdateReceiver", "Context or intent is null")
            onFail()
            return
        }

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        if (UpdateManager.currentId != id) {
            Log.d("UpdateReceiver", "download ids don't match")
            onFail()
            return
        }

        val apkFile = File(context.getExternalFilesDir(
            Environment.DIRECTORY_DOWNLOADS), "scoutify_update.apk"
        )

        if (apkFile.exists()) {
            context.unregisterReceiver(this)

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            context.startActivity(installIntent)
        } else {
            onFail()
        }
    }


}