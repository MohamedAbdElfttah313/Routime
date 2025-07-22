package com.example.routime.Data.ReposImp

import android.content.Context
import com.example.routime.Data.DataSource.DataBase.RoutimeDataBase
import com.example.routime.Data.Models.Category
import com.example.routime.Domain.Repos.CategoryRepo
import kotlinx.coroutines.flow.Flow

class CategoryRepoImp(context : Context)  : CategoryRepo {

    private val categoryDao = RoutimeDataBase.getInstance(context).CategoryDao()

    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    override fun insertCategory(category: Category) = categoryDao.insertCategory(category)
}