package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.ReposImp.DeedRepoImp
import com.example.routime.Domain.Repos.DeedRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GetDeedByMoodUseCase(context  : Context) {
    private val deedRepo : DeedRepo = DeedRepoImp(context)

    operator fun invoke(mood : String) = deedRepo.getDeedByMood(mood).flowOn(Dispatchers.IO)
}