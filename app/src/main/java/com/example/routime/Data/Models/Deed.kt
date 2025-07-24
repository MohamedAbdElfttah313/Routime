package com.example.routime.Data.Models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.routime.Emojis
import com.example.routime.R
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Deed")
data class Deed(
    @ColumnInfo(name = "title") val title : String,
    @ColumnInfo(name = "description") val description : String,
    @ColumnInfo(name = "start_time") val startTime : String,
    @ColumnInfo(name = "time_spent") val timeSpent : Int,
    @ColumnInfo(name = "category") val category : String,
    @ColumnInfo(name = "progress") val progress : Int,
    @ColumnInfo(name = "done") val done : Boolean,
    @ColumnInfo(name = "attachment_uri") val attachmentUri : String,
    @ColumnInfo(name = "attachment_type") val attachmentType : String,
    @ColumnInfo(name = "attachment_display_name") val attachmentDisplayName : String,
    @ColumnInfo(name = "icon_id") val iconId : Int = R.drawable.outline_box_24,
    @ColumnInfo(name = "mood_emoji_id") val emojiId : String= Emojis.HAPPY.code,
    @PrimaryKey(autoGenerate = true) val id : Long? = null
):Parcelable
