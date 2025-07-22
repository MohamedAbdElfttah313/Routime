package com.example.routime.Domain.Repos

import com.example.routime.Data.Models.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepo {

    fun getAllCategories() : Flow<List<Category>>
    fun insertCategory(category : Category)
}