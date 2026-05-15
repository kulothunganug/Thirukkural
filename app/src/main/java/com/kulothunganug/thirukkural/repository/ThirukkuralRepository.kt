package com.kulothunganug.thirukkural.repository

import com.kulothunganug.thirukkural.dao.ThirukkuralDao

class ThirukkuralRepository(private val dao: ThirukkuralDao) {
    fun getAll() = dao.getAll()
    suspend fun getById(number: Int) = dao.getById(number)

    fun getPals() = dao.getPals()
    fun getIyals(pals: List<String>) = dao.getIyals(pals)
    fun getAdikarams(pals: List<String>, iyals: List<String>) = dao.getAdikarams(pals, iyals)
    fun getFilteredKurals(
        pals: List<String>, usePals: Boolean,
        iyals: List<String>, useIyals: Boolean,
        adikarams: List<String>, useAdikarams: Boolean
    ) = dao.getFilteredKurals(pals, usePals, iyals, useIyals, adikarams, useAdikarams)
}