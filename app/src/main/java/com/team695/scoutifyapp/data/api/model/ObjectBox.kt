package com.team695.scoutifyapp.data.api.model

import android.content.Context
import com.team695.scoutifyapp.data.api.model.Entities.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.objectbox.android.AndroidObjectBrowser

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context)
            .debugFlags(DebugFlags.LOG_QUERIES)
            .build()
        println("ObjectBox init called")

        // Start ObjectBrowser (dev only)
        val started = AndroidObjectBrowser(store).start(context)
        println("ObjectBrowser started? $started")
    }
}