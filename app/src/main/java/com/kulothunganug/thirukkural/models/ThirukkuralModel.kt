package com.kulothunganug.thirukkural.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "records")
data class ThirukkuralModel(

    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "Adhigaram_ID")
    val adhigaramId: Int,

    @ColumnInfo(name = "Paal")
    val paal: String,

    @ColumnInfo(name = "Iyal")
    val iyal: String,

    @ColumnInfo(name = "Adhigaram")
    val adhigaram: String,

    @ColumnInfo(name = "Kural")
    val kural: String,

    @ColumnInfo(name = "Transliteration")
    val transliteration: String,

    @ColumnInfo(name = "Vilakam")
    val vilakam: String,

    @ColumnInfo(name = "Couplet")
    val couplet: String,

    @ColumnInfo(name = "Chapter")
    val chapter: String,

    @ColumnInfo(name = "Section")
    val section: String,

    @ColumnInfo(name = "Kalaingar_Urai")
    val kalaingarUrai: String,

    @ColumnInfo(name = "Parimezhalagar_Urai")
    val parimezhalagarUrai: String,

    @ColumnInfo(name = "M_Varadharajanar")
    val mVaradharajanar: String,

    @ColumnInfo(name = "Solomon_Pappaiya")
    val solomonPappaiya: String
)
