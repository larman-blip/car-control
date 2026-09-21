package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "inspections")
data class CarInspection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val fuelType: String,
    val isTurbo: Boolean,
    val transmission: String,
    val mileage: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val overallScore: Int = 100,
    val verdict: String = "BUY", // BUY, NEGOTIATE, AVOID
    val scoresJson: String = "{}", // e.g. {"DOCUMENTS": 10, "BODYWORK": 20, ...}
    val responsesJson: String = "{}", // e.g. {"doc_vin": "YES", "body_alignment": "WARNING"}
    val dtcCodesJson: String = "[]", // e.g. ["P0300", "P0420"]
    val expertPhone: String = "+33600000000"
)

@Dao
interface CarInspectionDao {
    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<CarInspection>>

    @Query("SELECT * FROM inspections WHERE id = :id LIMIT 1")
    suspend fun getInspectionById(id: Int): CarInspection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: CarInspection): Long

    @Query("DELETE FROM inspections WHERE id = :id")
    suspend fun deleteInspectionById(id: Int)

    @Query("DELETE FROM inspections")
    suspend fun deleteAll()
}

@Database(entities = [CarInspection::class], version = 1, exportSchema = false)
abstract class CarCheckDatabase : RoomDatabase() {
    abstract fun inspectionDao(): CarInspectionDao

    companion object {
        @Volatile
        private var INSTANCE: CarCheckDatabase? = null

        fun getDatabase(context: Context): CarCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarCheckDatabase::class.java,
                    "car_check_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class CarInspectionRepository(private val inspectionDao: CarInspectionDao) {
    val allInspections: Flow<List<CarInspection>> = inspectionDao.getAllInspections()

    suspend fun getInspectionById(id: Int): CarInspection? {
        return inspectionDao.getInspectionById(id)
    }

    suspend fun insert(inspection: CarInspection): Long {
        return inspectionDao.insertInspection(inspection)
    }

    suspend fun deleteById(id: Int) {
        inspectionDao.deleteInspectionById(id)
    }
}
