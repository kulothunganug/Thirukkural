package com.kulothunganug.thirukkural.dao

import androidx.room.Dao
import androidx.room.Query
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ThirukkuralDao {

    @Query("SELECT * FROM records")
    fun getAll(): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM records WHERE Paal = :paal")
    fun getByPaal(paal: String): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM records WHERE Adhigaram_ID = :id")
    fun getByAdhigaram(id: Int): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM records WHERE Kural = :number")
    suspend fun getByKural(number: Int): ThirukkuralModel
}