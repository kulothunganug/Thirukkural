package com.kulothunganug.thirukkural

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
@Entity(tableName = "records")
data class ThirukkuralEntity(

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

@Dao
interface ThirukkuralDao {

    @Query("SELECT * FROM records")
    fun getAll(): Flow<List<ThirukkuralEntity>>

    @Query("SELECT * FROM records WHERE Paal = :paal")
    fun getByPaal(paal: String): Flow<List<ThirukkuralEntity>>

    @Query("SELECT * FROM records WHERE Adhigaram_ID = :id")
    fun getByAdhigaram(id: Int): Flow<List<ThirukkuralEntity>>

    @Query("SELECT * FROM records WHERE Kural = :number")
    suspend fun getByKural(number: Int): ThirukkuralEntity
}


class ThirukkuralViewModel(
    private val dao: ThirukkuralDao
) : ViewModel() {

    fun kuralsByAdhigaram(adhigaramId: Int) =
        dao.getByAdhigaram(adhigaramId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}

class ThirukkuralViewModelFactory(
    private val dao: ThirukkuralDao
) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThirukkuralViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThirukkuralViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Database(
    entities = [ThirukkuralEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ThirukkuralDatabase : RoomDatabase() {

    abstract fun dao(): ThirukkuralDao

    companion object {
        @Volatile
        private var INSTANCE: ThirukkuralDatabase? = null

        fun get(context: Context): ThirukkuralDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ThirukkuralDatabase::class.java,
                    "thirukkural.db"
                )
                    .createFromAsset("thirukkural.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}


