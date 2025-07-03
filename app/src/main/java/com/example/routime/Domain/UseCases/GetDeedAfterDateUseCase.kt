package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.Models.Deed
import com.example.routime.Data.ReposImp.DeedRepoImp
import com.example.routime.Domain.Repos.DeedRepo
import com.example.routime.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

class GetDeedAfterDateUseCase(context : Context) {
    private val deedRepo : DeedRepo = DeedRepoImp(context)

    operator fun invoke(timeInMillis : Long):Flow<List<Deed>>{
        val dateFormattedForDb = Utils.formatTimeForDataBase(Date(timeInMillis))
        return deedRepo.getDeedAfterDate(dateFormattedForDb).flowOn(Dispatchers.IO)
    }
}