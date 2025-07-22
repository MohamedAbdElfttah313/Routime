package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.ReposImp.CategoryRepoImp
import com.example.routime.Domain.Repos.CategoryRepo

class GetAllCategoriesUseCase(context : Context) {

    private val categoryRepo : CategoryRepo = CategoryRepoImp(context)

    operator fun invoke() = categoryRepo.getAllCategories()
}