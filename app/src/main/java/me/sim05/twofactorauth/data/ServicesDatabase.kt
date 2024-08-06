package me.sim05.twofactorauth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Service::class], exportSchema = false)
abstract class ServicesDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var Instance: ServicesDatabase? = null

        fun getDatabase(context: Context): ServicesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ServicesDatabase::class.java, "service_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}