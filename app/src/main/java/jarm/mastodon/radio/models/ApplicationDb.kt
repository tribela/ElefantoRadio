package jarm.mastodon.radio.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    Application::class,
    Account::class
], version = 1)
abstract class ApplicationDb : RoomDatabase() {

    abstract fun applicationDao(): ApplicationDao
    abstract fun accountDao(): AccountDao

    companion object {
        private var INSTANCE: ApplicationDb? = null

        fun getInstance(context: Context): ApplicationDb? {
            if (INSTANCE == null) {
                synchronized(Application::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ApplicationDb::class.java,
                        "application.db"
                    )
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}