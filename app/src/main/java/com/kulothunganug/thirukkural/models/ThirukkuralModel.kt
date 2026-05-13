package com.kulothunganug.thirukkural.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thirukkural")
data class ThirukkuralModel(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "iyal_ta")
    val iyalTa: String,

    @ColumnInfo(name = "iyal_en")
    val iyalEn: String,

    @ColumnInfo(name = "iyal_tl")
    val iyalTl: String,

    @ColumnInfo(name = "pal_ta")
    val palTa: String,

    @ColumnInfo(name = "pal_en")
    val palEn: String,

    @ColumnInfo(name = "pal_tl")
    val palTl: String,

    @ColumnInfo(name = "adikaram_ta")
    val adikaramTa: String,

    @ColumnInfo(name = "adikaram_en")
    val adikaramEn: String,

    @ColumnInfo(name = "adikaram_tl")
    val adikaramTl: String,

    @ColumnInfo(name = "kural_ta")
    val kuralTa: String,

    @ColumnInfo(name = "kural_tl")
    val kuralTl: String,

    @ColumnInfo(name = "translation_en")
    val translationEn: String,

    @ColumnInfo(name = "couplet")
    val couplet: String,

    @ColumnInfo(name = "explanation_en")
    val explanationEn: String,

    @ColumnInfo(name = "explanation_ta")
    val explanationTa: String,

    @ColumnInfo(name = "commentary_mu_varatharasanar")
    val commentaryMuVaratharasanar: String,

    @ColumnInfo(name = "commentary_parimelazhagar")
    val commentaryParimelazhagar: String,

    @ColumnInfo(name = "commentary_salaman_pappaiya")
    val commentarySalamanPappaiya: String,

    @ColumnInfo(name = "commentary_manakudavar")
    val commentaryManakudavar: String,

    @ColumnInfo(name = "commentary_munusami")
    val commentaryMunusami: String,

    @ColumnInfo(name = "commentary_karunanidhi")
    val commentaryKarunanidhi: String
)