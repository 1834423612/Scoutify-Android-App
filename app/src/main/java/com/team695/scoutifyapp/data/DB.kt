//package com.team695.scoutifyapp.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [Match::class], version = 1, exportSchema = false)
//abstract class DB : RoomDatabase() {
//
//    abstract fun matchDAO(): MatchDAO
//
//    companion object {
//        @Volatile
//        private var Instance: DB? = null
//
//        fun getDatabase(context: Context): DB {
//            // if the Instance is not null, return it, otherwise create a new database instance.
//            return Instance ?: synchronized(this) {
//                Room.databaseBuilder(context, DB::class.java, "DB")
//                    .build()
//                    .also { Instance = it }
//            }
//        }
//    }
//}
package com.team695.scoutifyapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Match::class], version = 2, exportSchema = false)
abstract class DB : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO

    companion object {
        @Volatile
        private var Instance: DB? = null

        fun getDatabase(context: Context): DB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DB::class.java, "DB")
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Prepopulate on first creation
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).matchDAO()

                                dao.insert(
                                    Match(
                                        id = 1, teamNumber = 695
                                    )
                                )

                                dao.insert(
                                    Match(
                                        id = 2986,
                                        teamNumber = 1114,
                                    )
                                )
                            }
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
