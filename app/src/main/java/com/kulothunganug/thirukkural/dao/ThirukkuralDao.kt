package com.kulothunganug.thirukkural.dao

import androidx.room.Dao
import androidx.room.Query
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ThirukkuralDao {

    @Query("SELECT * FROM thirukkural")
    fun getAll(): Flow<List<ThirukkuralModel>>

    @Query("SELECT * FROM thirukkural WHERE id = :id")
    suspend fun getById(id: Int): ThirukkuralModel

    @Query("SELECT DISTINCT pal_ta FROM thirukkural")
    fun getPals(): Flow<List<String>>

    @Query("SELECT DISTINCT iyal_ta FROM thirukkural WHERE pal_ta IN (:pals)")
    fun getIyals(pals: List<String>): Flow<List<String>>

    @Query("SELECT DISTINCT adikaram_ta FROM thirukkural WHERE pal_ta IN (:pals) AND iyal_ta IN (:iyals)")
    fun getAdikarams(pals: List<String>, iyals: List<String>): Flow<List<String>>

    @Query("SELECT * FROM thirukkural WHERE (:usePals = 0 OR pal_ta IN (:pals)) AND (:useIyals = 0 OR iyal_ta IN (:iyals)) AND (:useAdikarams = 0 OR adikaram_ta IN (:adikarams))")
    fun getFilteredKurals(
        pals: List<String>, usePals: Boolean,
        iyals: List<String>, useIyals: Boolean,
        adikarams: List<String>, useAdikarams: Boolean
    ): Flow<List<ThirukkuralModel>>
}