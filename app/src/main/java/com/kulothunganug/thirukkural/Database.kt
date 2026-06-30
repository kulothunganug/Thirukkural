package com.kulothunganug.thirukkural

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kulothunganug.thirukkural.dao.ThirukkuralDao
import com.kulothunganug.thirukkural.models.ThirukkuralModel


@Database(
    entities = [ThirukkuralModel::class],
    version = 2,
    exportSchema = false
)
abstract class ThirukkuralDatabase : RoomDatabase() {

    abstract fun dao(): ThirukkuralDao

    companion object {
        @Volatile
        private var INSTANCE: ThirukkuralDatabase? = null

        fun get(context: Context): ThirukkuralDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                                context.applicationContext,
                                ThirukkuralDatabase::class.java,
                                "thirukkural.db"
                            ).fallbackToDestructiveMigration(false)
                    .createFromAsset("thirukkural.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}


