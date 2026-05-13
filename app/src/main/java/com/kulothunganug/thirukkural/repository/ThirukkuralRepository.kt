package com.kulothunganug.thirukkural.repository

import com.kulothunganug.thirukkural.dao.ThirukkuralDao

class ThirukkuralRepository(private val dao: ThirukkuralDao) {
    fun getAll() = dao.getAll()
    suspend fun getById(number: Int) = dao.getById(number)
}