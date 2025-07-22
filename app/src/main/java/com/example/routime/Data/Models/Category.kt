package com.example.routime.Data.Models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Category")
data class Category (
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "type") val type : String,
    @ColumnInfo(name = "iconId") val iconId : Int,
    @PrimaryKey(autoGenerate = true) val id : Long? = null
) : Parcelable