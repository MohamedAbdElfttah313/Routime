package com.example.routime.Data.DataSource.DataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.routime.Data.Models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    fun insertCategory(category : Category)

    @Update
    fun updateCategory(category : Category)

    @Delete
    fun deleteCategory(category:Category)

    @Query("SELECT * FROM Category")
    fun getAllCategories() : Flow<List<Category>>

}