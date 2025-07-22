package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.Models.Category
import com.example.routime.Data.ReposImp.CategoryRepoImp
import com.example.routime.Domain.Repos.CategoryRepo

class InsertNewCategoryUseCase(context : Context) {
    private val categoryRepo : CategoryRepo = CategoryRepoImp(context)

    operator fun invoke(category : Category) = categoryRepo.insertCategory(category)
}