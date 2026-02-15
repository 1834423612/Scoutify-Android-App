//package com.team695.scoutifyapp
//
//import android.app.Application
//import app.cash.sqldelight.driver.android.AndroidSqliteDriver
//import com.team695.scoutifyapp.db.AppDatabase
//
////import com.team695.scoutifyapp.data.api.model.ObjectBox
//
//class ScoutifyApp : Application() {
//    lateinit var database: AppDatabase
//        private set
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val driver = AndroidSqliteDriver(
//            schema = AppDatabase.Schema,
//            context = this,
//            name = "app.db"
//        )
//
//        database = AppDatabase(driver)
//    }
//}