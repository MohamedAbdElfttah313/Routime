package com.example.routime.Presentation.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routime.Data.Models.Deed
import com.example.routime.Domain.UseCases.GetDeedAfterDateUseCase
import com.example.routime.Domain.UseCases.GetDeedByCategoryUseCase
import com.example.routime.Domain.UseCases.GetDeedByMoodUseCase
import com.example.routime.Domain.UseCases.GetDeedBySearchQueryUseCase
import com.example.routime.Domain.UseCases.GetDeedOfDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchDeedViewModel(context : Context):ViewModel() {

    private val getDeedAfterDateUseCase = GetDeedAfterDateUseCase(context)
    private val getDeedOfDayUseCase = GetDeedOfDayUseCase(context)
    private val getDeedBySearchQueryUseCase = GetDeedBySearchQueryUseCase(context)
    private val getDeedByCategoryUseCase = GetDeedByCategoryUseCase(context)
    private val getDeedByMoodUseCase = GetDeedByMoodUseCase(context)

    private val _deedList = MutableStateFlow<List<Deed>>(emptyList())
    val deedList = _deedList.asStateFlow()

    fun getLastSevenDayDeeds(timeInMillis : Long){
        viewModelScope.launch {
            getDeedAfterDateUseCase.invoke(timeInMillis).collectLatest {
                _deedList.value = it
            }
        }
    }

    fun getDeedsOfDay(timeInMillis : Long){
        viewModelScope.launch {
            getDeedOfDayUseCase.invoke(timeInMillis).collectLatest {
                _deedList.value = it
                println("getDeedOfDayUseCase.size = ${it.size}")
            }
        }
    }


    fun getDeedBySearchQuery(query : String){
        viewModelScope.launch {
            getDeedBySearchQueryUseCase.invoke(query).collectLatest {
                _deedList.value = it
                println("getDeedBySearchQueryUseCase.size = ${it.size}")
            }
        }
    }

    fun getDeedByCategory(category : String){
        viewModelScope.launch {
            getDeedByCategoryUseCase.invoke(category).collectLatest {
                _deedList.value = it
            }
        }
    }

    fun getDeedByMood(mood : String){
        viewModelScope.launch {
            getDeedByMoodUseCase.invoke(mood).collectLatest{
                _deedList.value = it
            }
        }
    }

    fun makeListEmpty(){
        _deedList.value = emptyList()
    }
}