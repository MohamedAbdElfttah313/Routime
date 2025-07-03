package com.example.routime.Domain.Repos

import com.example.routime.Data.Models.Deed
import kotlinx.coroutines.flow.Flow

interface DeedRepo {

    fun getTodayDeed(todayDate: String):Flow<List<Deed>>
    fun insertNewDeed(deed: Deed)
    fun getDeedAfterDate(date : String) : Flow<List<Deed>>
    fun getDeedOfDay(date : String) : Flow<List<Deed>>
    fun getDeedBySearchQuery(query : String) : Flow<List<Deed>>
    fun getDeedByCategory(category: String) : Flow<List<Deed>>
    fun getDeedByMood(mood : String) : Flow<List<Deed>>
    fun deleteDeed(deed : Deed)
    fun updateDeed(deed : Deed)
}