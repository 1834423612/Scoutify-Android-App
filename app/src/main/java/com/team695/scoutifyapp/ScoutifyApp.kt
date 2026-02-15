package com.team695.scoutifyapp

import android.app.Application
//import com.team695.scoutifyapp.data.api.model.ObjectBox

class ScoutifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        println("ScoutifyApp: onCreate START")
        //ObjectBox.init(this)
        println("ScoutifyApp: ObjectBox.init DONE")
    }
}