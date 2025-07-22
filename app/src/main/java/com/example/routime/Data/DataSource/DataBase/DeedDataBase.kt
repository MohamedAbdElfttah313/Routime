package com.example.routime.Data.DataSource.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.routime.Data.Models.Category
import com.example.routime.Data.Models.Deed

@Database(entities = [Deed::class,Category::class], version = 1)
abstract class RoutimeDataBase : RoomDatabase() {
    abstract fun DeedDao() : DeedDao
    abstract fun CategoryDao() : CategoryDao

    companion object{
        
        @Volatile
        private var INSTANCE : RoutimeDataBase? = null

        fun getInstance(context : Context): RoutimeDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutimeDataBase::class.java,
                    "APPLICATION_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
