package com.team695.scoutifyapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Match)

    @Update
    suspend fun update(item: Match)

    @Delete
    suspend fun delete(item: Match)

    @Query("SELECT * from Matches WHERE id = :id")
    fun getItem(id: Int): Flow<Match?>

    @Query("SELECT * from Matches ORDER BY teamNumber ASC")
    fun getAllItems(): Flow<List<Match>>
}