package com.example.routime.Presentation.ViewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.routime.Data.Models.Deed
import com.example.routime.Domain.UseCases.DeleteDeedUseCase
import com.example.routime.Domain.UseCases.InsertNewDeedUseCase

class ShowDeedViewModel(context: Context) : ViewModel() {

    private val deleteDeedUseCase = DeleteDeedUseCase(context)

    fun deleteDeed(deed:Deed) = deleteDeedUseCase.invoke(deed)

}