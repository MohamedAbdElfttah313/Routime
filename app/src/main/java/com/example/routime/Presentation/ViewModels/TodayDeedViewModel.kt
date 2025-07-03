package com.example.routime.Presentation.ViewModels

import android.content.Context
import android.renderscript.RenderScript.ContextType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routime.Data.Models.Deed
import com.example.routime.Domain.UseCases.GetTodayDeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodayDeedViewModel(context : Context) : ViewModel() {

    private val _todayDeeds = MutableStateFlow<List<Deed>>(emptyList())
    val todayDeeds = _todayDeeds.asStateFlow()

    private val getTodayDeedUseCase = GetTodayDeedUseCase(context)

    init {
        viewModelScope.launch {
            getTodayDeedUseCase.execute().collect{
                    _todayDeeds.value = it
                }
        }
    }

}
