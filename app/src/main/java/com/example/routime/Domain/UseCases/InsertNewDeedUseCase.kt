package com.example.routime.Domain.UseCases

import android.content.Context
import com.example.routime.Data.Models.Deed
import com.example.routime.Data.ReposImp.DeedRepoImp
import com.example.routime.Domain.Repos.DeedRepo

class InsertNewDeedUseCase(context: Context) {
    private val deedRepo : DeedRepo = DeedRepoImp(context)

    operator fun invoke(deed:Deed) = deedRepo.insertNewDeed(deed)
}