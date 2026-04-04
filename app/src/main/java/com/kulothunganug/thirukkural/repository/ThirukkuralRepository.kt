package com.kulothunganug.thirukkural.repository

import com.kulothunganug.thirukkural.dao.ThirukkuralDao

class ThirukkuralRepository(private val dao: ThirukkuralDao) {
    fun getAll() = dao.getAll()
    fun getByPaal(paal: String) = dao.getByPaal(paal)
    fun getByAdhigaram(id: Int) = dao.getByAdhigaram(id)
    suspend fun getByKural(number: Int) = dao.getByKural(number)
}