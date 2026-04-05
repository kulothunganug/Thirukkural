package com.kulothunganug.thirukkural.repository

import com.kulothunganug.thirukkural.dao.ThirukkuralDao

class ThirukkuralRepository(private val dao: ThirukkuralDao) {
    fun getAll() = dao.getAll()
    fun getByPaal(paal: String) = dao.getByPaal(paal)
    fun getByAdhigaramId(id: Int) = dao.getByAdhigaramId(id)
    suspend fun getById(number: Int) = dao.getById(number)
}