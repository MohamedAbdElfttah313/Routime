package com.example.routime.Presentation.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.routime.Data.Models.Deed
import com.example.routime.Domain.UseCases.InsertNewDeedUseCase
import com.example.routime.Domain.UseCases.UpdateDeedUseCase

class AddNewDeedViewModel(context : Context) : ViewModel() {
    private val insertNewDeedUseCase = InsertNewDeedUseCase(context)
    private val updateDeedUseCase = UpdateDeedUseCase(context)

    fun insertNewDeed(deed : Deed){
        insertNewDeedUseCase.invoke(deed)
    }

    fun updateDeed(deed : Deed){
        updateDeedUseCase.invoke(deed)
    }

}
