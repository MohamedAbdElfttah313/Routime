package com.example.routime.Presentation.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routime.Data.Models.Category
import com.example.routime.Domain.UseCases.GetAllCategoriesUseCase
import com.example.routime.Domain.UseCases.InsertNewCategoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(context : Context) : ViewModel(){

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList = _categoryList.asStateFlow()

    private val getAllCategoriesUseCase = GetAllCategoriesUseCase(context)
    private val insertNewCategoryUseCase = InsertNewCategoryUseCase(context)


    init {
        viewModelScope.launch {
            getAllCategoriesUseCase.invoke().collect{
                _categoryList.value = it
            }
        }
    }

    fun insertNewCategory(category : Category){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                insertNewCategoryUseCase(category)
            }
        }
    }
}