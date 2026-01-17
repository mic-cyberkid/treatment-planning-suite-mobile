package com.mosec.tpsuite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user in the Treatment Planning Suite.
 * Persisted in the Room database.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val fullName: String = "",
    val password: String? = null,
    val role: String // "Physicist", "Dosimetrist", "Administrator"
)
