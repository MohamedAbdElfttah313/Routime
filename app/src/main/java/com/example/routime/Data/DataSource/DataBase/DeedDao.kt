package com.example.routime.Data.DataSource.DataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.routime.Data.Models.Deed
import kotlinx.coroutines.flow.Flow

@Dao
interface DeedDao {

    @Insert
    fun insertDeed(deed : Deed)

    @Update
    fun updateDeed(deed : Deed)

    @Query("SELECT * FROM deed ORDER BY start_time DESC")
    fun getAllDeed() : Flow<List<Deed>>

    @Query("SELECT * FROM Deed WHERE start_time LIKE :date ORDER BY start_time DESC")
    fun getDeedsOfDay(date : String): Flow<List<Deed>>

    @Query("SELECT * FROM Deed WHERE category = :category ORDER BY start_time DESC")
    fun getDeedByCategory(category : String) : Flow<List<Deed>>

    @Query("SELECT * FROM Deed WHERE mood_emoji_id = :mood ORDER BY start_time DESC")
    fun getDeedByMood(mood : String) : Flow<List<Deed>>

    @Query("SELECT * FROM Deed WHERE start_time > :date ORDER BY start_time DESC")
    fun getDeedsAfterDate(date : String): Flow<List<Deed>>

    @Query("SELECT * FROM Deed ORDER BY time_spent ASC")
    fun getDeedsByTimeSpentOrderedAsc():Flow<List<Deed>>

    @Query("SELECT * FROM Deed ORDER BY time_spent DESC")
    fun getDeedsByTimeSpentOrderedDesc():Flow<List<Deed>>

    @Query("SELECT * FROM Deed WHERE title LIKE :term " +
            "OR category LIKE :term " +
            "OR description LIKE :term " +
            "ORDER BY start_time ASC")
    fun getDeedBySearchTerm(term : String= "%%"):Flow<List<Deed>>

    @Delete
    fun deleteDeed(deed : Deed)
}
