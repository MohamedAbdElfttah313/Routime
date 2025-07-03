package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.Models.Deed
import com.example.routime.Data.ReposImp.DeedRepoImp
import com.example.routime.Domain.Repos.DeedRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetTodayDeedUseCase(context : Context) {
    private val deedRepo : DeedRepo = DeedRepoImp(context)


    fun execute() : Flow<List<Deed>>{
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())+"%"
        return deedRepo.getTodayDeed(todayDate).flowOn(Dispatchers.IO)
    }

}
