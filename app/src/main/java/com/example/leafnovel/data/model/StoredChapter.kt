package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class StoredChapter(
    @NonNull
    var bookId: String,
    @NonNull
    @ColumnInfo(name = "title")
    var chapterTitle: String,
    @ColumnInfo(name = "content")
    var chapterContent: String,
    @NonNull
    var index: Int,
    @NonNull
    @ColumnInfo(name = "progress")
    var readProgress: Int ,
    @NonNull
    var isFavorite: Boolean,
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
) : Parcelable {}


