package com.example.routime.Data.ReposImp

import android.content.Context
import com.example.routime.Data.DataSource.DataBase.RoutimeDataBase
import com.example.routime.Data.Models.Deed
import com.example.routime.Domain.Repos.DeedRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class DeedRepoImp(context : Context) : DeedRepo{

    private val deedDao = RoutimeDataBase.getInstance(context).DeedDao()

    override fun getTodayDeed(todayDate : String) = deedDao.getDeedsOfDay(todayDate).flowOn(Dispatchers.IO)

    override fun insertNewDeed(deed: Deed) = deedDao.insertDeed(deed)

    override fun getDeedAfterDate(date: String) =  deedDao.getDeedsAfterDate(date)

    override fun getDeedOfDay(date: String) = deedDao.getDeedsOfDay(date)

    override fun getDeedBySearchQuery(query: String) =deedDao.getDeedBySearchTerm(query)

    override fun getDeedByCategory(category: String) = deedDao.getDeedByCategory(category)

    override fun getDeedByMood(mood : String) = deedDao.getDeedByMood(mood)

    override fun deleteDeed(deed: Deed) = deedDao.deleteDeed(deed)

    override fun updateDeed(deed: Deed) = deedDao.updateDeed(deed)
}