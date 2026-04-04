package com.kulothunganug.thirukkural

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kulothunganug.thirukkural.dao.ThirukkuralDao
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


@Database(
    entities = [ThirukkuralModel::class],
    version = 1,
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
                )
                    .createFromAsset("thirukkural.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}


