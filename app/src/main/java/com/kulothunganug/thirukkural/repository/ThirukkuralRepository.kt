package com.kulothunganug.thirukkural.repository

import com.kulothunganug.thirukkural.dao.ThirukkuralDao

class ThirukkuralRepository(private val dao: ThirukkuralDao) {
    suspend fun getById(number: Int) = dao.getById(number)
    suspend fun getByIds(numbers: List<Int>) = dao.getByIds(numbers)

    fun getPals() = dao.getPals()
    suspend fun getIdsFiltered(
        pals: List<String>, usePals: Boolean,
        iyals: List<String>, useIyals: Boolean,
        adikarams: List<String>, useAdikarams: Boolean
    ) = dao.getIdsFiltered(pals, usePals, iyals, useIyals, adikarams, useAdikarams)
    fun getIyals(pals: List<String>) = dao.getIyals(pals)
    fun getAdikarams(pals: List<String>, iyals: List<String>) = dao.getAdikarams(pals, iyals)
    fun getFilteredKurals(
        pals: List<String>, usePals: Boolean,
        iyals: List<String>, useIyals: Boolean,
        adikarams: List<String>, useAdikarams: Boolean
    ) = dao.getFilteredKurals(pals, usePals, iyals, useIyals, adikarams, useAdikarams)
}