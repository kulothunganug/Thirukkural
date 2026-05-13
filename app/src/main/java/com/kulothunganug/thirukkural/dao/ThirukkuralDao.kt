package com.kulothunganug.thirukkural.dao

import androidx.room.Dao
import androidx.room.Query
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ThirukkuralDao {

    @Query("SELECT * FROM thirukkural")
    fun getAll(): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM thirukkural WHERE pal_ta = :pal")
    fun getByPal(pal: String): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM thirukkural WHERE id = :id")
    suspend fun getById(id: Int): ThirukkuralModel
}