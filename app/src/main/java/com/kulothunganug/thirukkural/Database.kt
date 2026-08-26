package com.kulothunganug.thirukkural

import androidx.room.Database
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
}
