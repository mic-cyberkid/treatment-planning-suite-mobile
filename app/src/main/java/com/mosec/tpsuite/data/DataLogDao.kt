package com.mosec.tpsuite.data

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface DataLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DataLog)

    @Query("SELECT * FROM data_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DataLog>>

    @Query("DELETE FROM data_logs")
    suspend fun deleteAllLogs()
    suspend fun getLogs() {
        TODO("Not yet implemented")
    }
}
