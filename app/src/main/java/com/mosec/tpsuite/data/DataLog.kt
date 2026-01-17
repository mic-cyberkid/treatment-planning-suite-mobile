package com.mosec.tpsuite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_logs")
data class DataLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val calculationType: String = "LINAC QA",
    val calculationValues: String, // JSON string
    val result: String,
    val date: Long = System.currentTimeMillis()
)
